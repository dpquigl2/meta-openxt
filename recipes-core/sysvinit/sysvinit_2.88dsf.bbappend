# USE_VT and SERIAL_CONSOLE are generally defined by the MACHINE .conf.
# Set PACKAGE_ARCH appropriately.
PACKAGE_ARCH_${PN}-inittab = "${MACHINE_ARCH}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"
