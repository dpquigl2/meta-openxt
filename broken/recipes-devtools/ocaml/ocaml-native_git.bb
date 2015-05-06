SECTION = "devel"
LICENSE = "QPL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=524443efef4a3e092cca058d99996c88"

PR .= "+xc1"

PV = "0+git${SRCPV}"


SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/ocaml/ocaml.git"

inherit openxt native autotools-brokensep

S = "${WORKDIR}/git"

RDEPENDS_${PN}-dev = ""

do_compile() {
	oe_runmake world.opt
}


do_package[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_rpm[noexec] = "1"
do_package_write_deb[noexec] = "1"

