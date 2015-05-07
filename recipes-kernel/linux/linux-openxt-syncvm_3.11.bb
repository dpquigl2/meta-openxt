DESCRIPTION = "Linux kernel XenClient syncvm"
COMPATIBLE_MACHINE = "(openxt-syncvm)"

require linux-openxt-${PV}.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-3.11:"
