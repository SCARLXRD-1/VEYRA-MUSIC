# Copyright (C) 2026 SFG545
#
# This file is part of Veyra Music.
#
# Veyra Music es un fork de Orchard (https://github.com/SFG5453/Orchard),
# mantenido únicamente para consumo personal y sin afiliación con el
# proyecto original.
#
# Veyra Music is free software: you can redistribute it and/or modify it under the
# terms of the GNU Affero General Public License as published by the Free
# Software Foundation, either version 3 of the License, or (at your option) any
# later version.
#
# Veyra Music is distributed in the hope that it will be useful, but WITHOUT ANY
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
# PARTICULAR PURPOSE. See the GNU Affero General Public License for more
# details.
#
# You should have received a copy of the GNU Affero General Public License
# along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.

# Convierte el checkpoint "vocals" de umxhq (pesos con licencia MIT, Inria/SigSep,
# entrenado en MUSDB18-HQ) a ONNX con un recuento de frames fijo. No lo ejecuta
# automáticamente ningún paso de build; el modelo int8 derivado está commiteado en
# models/vocal-separation/vocals_umxhq_int8.onnx, así que los builds normales y CI
# nunca necesitan PyTorch. Este script existe para que la conversión sea reproducible
# en lugar de un artefacto puntual, y para documentar exactamente cómo se produjo el
# modelo distribuido.
#
# Uso (desde un venv con torch, torchaudio, onnx y onnxruntime instalados, y
# PYTHONPATH apuntando a un checkout de github.com/sigsep/open-unmix-pytorch):
#
#   curl -sL -o vocals-umxhq.pth \
#     https://zenodo.org/records/3370489/files/vocals-b62c91ce.pth
#   # sha256: b62c91cedbc7a066f1778ead5b5cecb377aa3a46a31af1cce7c5c8769339d083
#   PYTHONPATH=./open-unmix-pytorch python convert-umx-vocals-to-onnx.py \
#     vocals-umxhq.pth vocals_umxhq.onnx
#   python -c "from onnxruntime.quantization import quantize_dynamic, QuantType; \
#     quantize_dynamic('vocals_umxhq.onnx', 'vocals_umxhq_int8.onnx', weight_type=QuantType.QInt8)"
#
# Los ejes dinámicos de frames no sobreviven aquí al trazado de TorchScript: el
# forward() de OpenUnmix desempaqueta `nb_frames, nb_samples, ... = x.data.shape`
# como enteros Python normales y hace reshape contra ellos directamente, así que el
# trazado fija el recuento de frames de la entrada dummy como constante del grafo
# sin importar lo que declare `dynamic_axes`; intentarlo produce un modelo que de
# todos modos solo corre exactamente con el recuento de frames trazado, solo que sin
# que eso sea explícito. Una forma fija evita el bug de raíz, y el uso de Veyra
# Music es siempre una rebanada de solapamiento corta y acotada (<=16 beats más
# padding), así que un presupuesto fijo -- como los chunks fijos de 1500 frames de
# Beat This -- encaja de forma natural en lugar de ser un parche.
import sys
import torch
from openunmix.model import OpenUnmix

# Covers MAX_OVERLAP_SECONDS (16, in wsolaPlanner.js) plus slice padding and
# margin, at the model's native hop of 1024 samples / 44100 Hz (~43 fps).
FIXED_FRAMES = 960

ckpt_path, out_path = sys.argv[1], sys.argv[2]
state = torch.load(ckpt_path, map_location='cpu', weights_only=False)

# The checkpoint's own tensors carry the bandwidth restriction the model was
# trained with; reading it from the state dict rather than hard-coding it
# keeps this script correct if a different checkpoint is ever substituted.
max_bin = state['input_mean'].shape[0]
nb_output_bins = state['output_scale'].shape[0]
model = OpenUnmix(
    nb_bins=nb_output_bins,
    nb_channels=2,
    hidden_size=512,
    nb_layers=3,
    max_bin=max_bin,
)
# The checkpoint also carries a couple of buffers (stft/transform windows)
# that belong to the full Separator wrapper, not this module; strict=False
# skips them without missing anything OpenUnmix itself needs.
missing, unexpected = model.load_state_dict(state, strict=False)
if missing:
    raise RuntimeError(f'checkpoint is missing required parameters: {missing}')
print('unexpected (expected, belong to the Separator wrapper):', unexpected)
model.eval()

dummy = torch.randn(1, 2, nb_output_bins, FIXED_FRAMES).abs()
with torch.no_grad():
    out = model(dummy)
print('output shape:', tuple(out.shape))

torch.onnx.export(
    model, dummy, out_path,
    input_names=['mix_magnitude'],
    output_names=['target_magnitude'],
    opset_version=17,
    # The new torch.export-based exporter (torch.onnx.export's default as of
    # PyTorch 2.9) failed on this model's rearrange/reshape pattern the same
    # way it did on Beat This's small checkpoint; the legacy TorchScript
    # tracer handles it correctly and is what parity was measured against.
    dynamo=False,
)
print('exported', out_path, 'at fixed frames =', FIXED_FRAMES)
