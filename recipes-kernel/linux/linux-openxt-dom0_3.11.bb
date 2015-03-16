DESCRIPTION = "Linux kernel XenClient dom0"
COMPATIBLE_MACHINE = "(openxt-dom0)"

require linux-openxt-${PV}.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-3.11"

