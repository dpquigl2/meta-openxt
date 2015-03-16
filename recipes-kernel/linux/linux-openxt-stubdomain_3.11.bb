DESCRIPTION = "Linux kernel XenClient stubdomain"
COMPATIBLE_MACHINE = "(openxt-stubdomain)"

require linux-openxt-${PV}.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-3.11"
