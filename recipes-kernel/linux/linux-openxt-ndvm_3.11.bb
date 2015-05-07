DESCRIPTION = "Linux kernel XenClient ndvm"
COMPATIBLE_MACHINE = "(openxt-ndvm)"

require linux-openxt-${PV}.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-3.11:"
