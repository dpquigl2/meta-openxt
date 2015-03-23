LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea116a7defaf0e93b3bb73b2a34a3f51"
DEPENDS = "openssl"
PR = "r2"

SRC_URI = "http://www.mmonit.com/monit/dist/monit-${PV}.tar.gz\
	file://init \
	file://monitrc \
	file://display_reboot"

INITSCRIPT_NAME = "monit"
INITSCRIPT_PARAMS = "start 99 5 . stop 00 0 1 6 ."

inherit autotools update-rc.d

EXTRA_OECONF = "--without-ssl --without-pam libmonit_cv_setjmp_available=yes libmonit_cv_vsnprintf_c99_conformant=yes"

do_install_append() {
	install -d ${D}${sysconfdir}/init.d/
	install -m 755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/monit
	install -m 600 ${WORKDIR}/monitrc ${D}${sysconfdir}/monitrc
	install -m 755 ${WORKDIR}/display_reboot ${D}/usr/bin/display_reboot
}

CONFFILES_${PN} += "${sysconfdir}/monitrc"


SRC_URI[md5sum] = "b11821062a43b951d73ccc16fcda939d"
SRC_URI[sha256sum] = "43075396203569f87b67f7bffd1de739aa2fba302956237a2b0dc7aaf62da343"

#The autoconf files are built with 2.4.2. OE-Core has 2.4.6 so we autoreconf unconditionally.
do_autoreconf() {
	cd ${S}; autoreconf -Wcross --install --force
}

addtask autoreconf before do_configure
