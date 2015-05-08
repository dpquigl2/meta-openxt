EXTRA_OECONF += " --enable-xen"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://xenclient-4.3-support.patch"
