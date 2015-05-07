DESCRIPTION = "Linux kernel XenClient uivm"
COMPATIBLE_MACHINE = "(openxt-uivm)"

require linux-openxt-${PV}.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-3.11:"
