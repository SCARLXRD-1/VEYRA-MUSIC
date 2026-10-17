/*
 * Copyright (C) 2026 SFG545
 *
 * This file is part of Veyra Music.
 *
 * Veyra Music es un fork de Orchard (https://github.com/SFG5453/Orchard),
 * mantenido únicamente para consumo personal y sin afiliación con el
 * proyecto original.
 *
 * Veyra Music is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Veyra Music is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
 */

Unicode True
ManifestSupportedOS win10
RequestExecutionLevel user
SetCompressor /SOLID lzma
BrandingText "Veyra Packages"
Icon "${ICON}"
UninstallIcon "${ICON}"

!include "MUI2.nsh"

Name "Veyra Music"
OutFile "${OUTPUT}"
InstallDir "$LOCALAPPDATA\Programs\Veyra Music"
InstallDirRegKey HKCU "Software\Veyra Packages" "InstallLocation"

VIProductVersion "${VERSION}.0"
VIFileVersion "${VERSION}.0"
VIAddVersionKey "ProductName" "Veyra Music"
VIAddVersionKey "FileVersion" "${VERSION}"
VIAddVersionKey "ProductVersion" "${VERSION}"
VIAddVersionKey "FileDescription" "Veyra Music package installer"
VIAddVersionKey "LegalCopyright" "Copyright (C) 2026 AKASHI DEV"

!define MUI_ABORTWARNING
!define MUI_ICON "${ICON}"
!define MUI_UNICON "${ICON}"
!define MUI_FINISHPAGE_RUN "$INSTDIR\veyra-packages.exe"
!define MUI_FINISHPAGE_RUN_TEXT "Launch Veyra Packages"

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "${LICENSE}"
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

!insertmacro MUI_LANGUAGE "English"

Section "Veyra Packages" VeyraPackages
  SetShellVarContext current
  RMDir /r "$INSTDIR"
  SetOutPath "$INSTDIR"
  File /r "${BUNDLE}\*"
  File /oname=veyra.cmd "${LAUNCHER}"
  File /oname=orchard.cmd "${LAUNCHER}"
  WriteUninstaller "$INSTDIR\Uninstall Veyra Music.exe"

  ; Clean up legacy shortcuts and registry keys
  Delete "$SMPROGRAMS\Orchard.lnk"
  Delete "$SMPROGRAMS\Orchard\Orchard.lnk"
  Delete "$DESKTOP\Orchard.lnk"
  RMDir "$SMPROGRAMS\Orchard"
  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\dev.sfg.orchard"
  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Orchard"
  DeleteRegKey HKCU "Software\Orchard Packages"

  Delete "$SMPROGRAMS\Veyra Music.lnk"
  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\dev.akashi.veyra"
  CreateDirectory "$SMPROGRAMS\Veyra Music"
  CreateShortCut "$SMPROGRAMS\Veyra Music\Veyra Music.lnk" "$INSTDIR\veyra-packages.exe" "" "$INSTDIR\veyra-packages.exe" 0 SW_SHOWNORMAL "" "Launch Veyra Music"
  CreateShortCut "$DESKTOP\Veyra Music.lnk" "$INSTDIR\veyra-packages.exe" "" "$INSTDIR\veyra-packages.exe" 0 SW_SHOWNORMAL "" "Launch Veyra Music"

  WriteRegStr HKCU "Software\Veyra Packages" "InstallLocation" "$INSTDIR"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "DisplayName" "Veyra Music"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "DisplayVersion" "${VERSION}"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "Publisher" "AKASHI DEV"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "InstallLocation" "$INSTDIR"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "DisplayIcon" "$INSTDIR\veyra-packages.exe"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "UninstallString" "$\"$INSTDIR\Uninstall Veyra Music.exe$\""
  WriteRegDWORD HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "NoModify" 1
  WriteRegDWORD HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music" "NoRepair" 1
SectionEnd

Section "Uninstall"
  SetShellVarContext current
  Delete "$SMPROGRAMS\Veyra Music.lnk"
  Delete "$SMPROGRAMS\Veyra Music\Veyra Music.lnk"
  Delete "$DESKTOP\Veyra Music.lnk"
  RMDir "$SMPROGRAMS\Veyra Music"
  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\dev.akashi.veyra"
  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Veyra Music"
  DeleteRegKey HKCU "Software\Veyra Packages"
  RMDir /r "$INSTDIR"
SectionEnd
