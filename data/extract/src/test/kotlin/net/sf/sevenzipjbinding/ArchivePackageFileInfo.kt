package net.sf.sevenzipjbinding

import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem

data class ArchivePackageFileInfo(
  val fileName: String,
  val parentPath: String?,
  val deep: Int,
  val path: String,
  val handle: ISimpleInArchiveItem, val isFolder: Boolean
)
