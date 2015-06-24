require xen.inc
require xen-arch.inc

inherit autotools-brokensep gettext setuptools pkgconfig update-rc.d pythonnative

SRC_URI += "file://xenstored.initscript \
	    file://xenconsoled.initscript \
	    file://config.patch \
	    file://disable-xen-root-check.patch \
	    file://do-not-overwrite-cc-and-ld.patch \
"
PACKAGECONFIG ??= " \
    sdl \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
    ${@bb.utils.contains('XEN_TARGET_ARCH', 'x86_64', 'hvm', '', d)} \
    "

PACKAGECONFIG[sdl] = "--enable-sdl,--disable-sdl,virtual/libsdl,"
PACKAGECONFIG[xsm] = "--enable-xsmpolicy,--disable-xsmpolicy,checkpolicy-native,"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd,"
PACKAGECONFIG[hvm] = "--with-system-seabios="${STAGING_DIR_HOST}/usr/share/firmware/bios.bin",--disable-seabios,seabios ipxe vgabios,"



DEPENDS += " gettext ncurses openssl python zlib seabios ipxe gmp lzo glib-2.0"
DEPENDS += "util-linux"
# lzo2 required by libxenguest.
RDEPENDS_${PN} += " lzo"

PACKAGES = "${PN}-libxenstore ${PN}-libxenstore-dev ${PN}-libxenstore-dbg ${PN}-libxenstore-staticdev   \
            ${PN}-xenstore-utils ${PN}-xenstore-utils-dbg ${PN}-libxenctrl ${PN}-libxenctrl-dev         \
            ${PN}-xenconsoled ${PN}-xenconsole 								\
	    ${PN}-libxenhvm ${PN}-libxenhvm-dev         						\
	    ${PN}-libxenguest ${PN}-libxenguest-dev         						\
	    ${PN}-libxenstat ${PN}-libxenstat-dev         						\
	    ${PN}-xenstored                                                                             \
            ${PN}-xenctx ${PN}-xenctx-dbg                                                               \
            ${PN}-xentrace                                                                              \
            ${PN}-xenpvnetboot                                                                          \
	    ${PN}-xenconsole-dbg							\
            ${PN} ${PN}-dbg ${PN}-doc ${PN}-dev ${PN}-staticdev                                         \
"

FILES_${PN}-xenconsole = "${libdir}/xen/bin/xenconsole"
FILES_${PN}-xenconsole-dbg = "${libdir}/xen/bin/.debug/xenconsole*"
RDEPENDS_${PN}-xenctx += "${PN}"

FILES_${PN}-xenctx = "${libdir}/xen/bin/xenctx"
FILES_${PN}-xenctx-dbg = "${libdir}/xen/bin/.debug/xenctx"
RDEPENDS_${PN}-xenctx += "${PN}"

FILES_${PN}-libxenstore = "${libdir}/libxenstore.so.*"
FILES_${PN}-libxenstore-dev = "${libdir}/libxenstore.so \
                               ${includedir}/xenstore*.h"
FILES_${PN}-libxenstore-dbg = "${libdir}/.debug/libxenstore.so*"
FILES_${PN}-libxenstore-staticdev = "${libdir}/libxenstore.a"

FILES_${PN}-libxenctrl = "${libdir}/libxenctrl.so.*"
FILES_${PN}-libxenctrl-dev = "${libdir}/libxenctrl.so"

FILES_${PN}-libxenhvm = "${libdir}/libxenhvm.so.*"
FILES_${PN}-libxenhvm-dev = "${libdir}/libxenhvm.so"

FILES_${PN}-libxenguest = "${libdir}/libxenguest.so.*"
FILES_${PN}-libxenguest-dev = "${libdir}/libxenguest.so"

FILES_${PN}-libxenstat = "${libdir}/libxenstat.so.*"
FILES_${PN}-libxenstat-dev = "${libdir}/libxenstat.so"


FILES_${PN}-xenstore-utils = "${bindir}/xenstore-*"
FILES_${PN}-xenstore-utils-dbg = "${bindir}/.debug/xenstore-*"
RDEPENDS_${PN}-xenstore-utils += "${PN}-libxenstore"

FILES_${PN}-xentrace = "${datadir}/xentrace"

FILES_${PN}-staticdev += "${libdir}/*.a"

FILES_${PN}-dbg += "\
    ${libdir}/.debug \
    ${libdir}/xen/bin/.debug \
    ${libdir}/python2.7/site-packages/.debug \
    ${libdir}/python2.7/site-packages/xen/lowlevel/.debug \
    ${libdir}/fs/xfs/.debug \
    ${libdir}/fs/ufs/.debug \
    ${libdir}/fs/ext2fs-lib/.debug \
    ${libdir}/fs/fat/.debug \
    ${libdir}/fs/zfs/.debug \
    ${libdir}/fs/reiserfs/.debug \
    ${libdir}/fs/iso9660/.debug \
    ${libdir}/fs/*/.debug \
    ${sbindir}/.debug \
    ${libdir}exec/.debug \
    ${libdir}/xen/libexec/.debug \
    ${bindir}/.debug \
    ${libdir}/python2.7/dist-packages/.debug \
    ${libdir}/python2.7/dist-packages/xen/lowlevel/.debug \
    "

FILES_${PN}-xenpvnetboot = "${libdir}/xen/bin/xenpvnetboot"

FILES_${PN} += "${datadir}/xen/qemu"
RDEPENDS_${PN} += "${PN}-xenstore-utils"

FILES_${PN}-xenstored = "${sysconfdir}/init.d/xenstored ${sbindir}/xenstored /*/*/xenstored"
FILES_${PN}-xenconsoled = "${sysconfdir}/init.d/xenconsoled ${sbindir}/xenconsoled /*/*/xenconsoled"
INITSCRIPT_PACKAGES = "${PN}-xenconsoled"
INITSCRIPT_NAME_${PN}-xenconsoled = "xenconsoled"
INITSCRIPT_PARAMS_${PN} = "defaults 60"

INITSCRIPT_NAME_${PN}-xenstored = "xenstored"
INITSCRIPT_PARAMS_${PN}-xenstored = "defaults 05"

#EXTRA_OEMAKE += "CROSS_SYS_ROOT=${STAGING_DIR_HOST} CROSS_COMPILE=${TARGET_PREFIX}"
EXTRA_OEMAKE += "CONFIG_IOEMU=n"
# Why is that last one necessary?

#### REQUIRED ENVIRONMENT VARIABLES ####
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

# specify xen hypervisor to build/target
export XEN_TARGET_ARCH = "${@map_xen_arch(d.getVar('TARGET_ARCH', True), d)}"
export XEN_COMPILE_ARCH = "${@map_xen_arch(d.getVar('BUILD_ARCH', True), d)}"

python () {
    if d.getVar('XEN_TARGET_ARCH', True) == 'INVALID':
        raise bb.parse.SkipPackage('Cannot map `%s` to a xen architecture' % d.getVar('TARGET_ARCH', True))
}

# hardcoded as Linux, as the only compatible hosts are Linux.
export XEN_OS = "Linux"

# this is used for the header (#!${bindir}/python) of the install python scripts
export PYTHONPATH="${bindir}/python"

# seabios forcefully sets HOSTCC to CC - fixup to allow it to build native conf executable
export HOSTCC="${BUILD_CC}"

# make xen requires CROSS_COMPILE set by hand as it does not abide by ./configure
export CROSS_COMPILE="${TARGET_PREFIX}"

# overide LDFLAGS to allow xen to build without: "x86_64-oe-linux-ld: unrecognized option '-Wl,-O1'"
export LDFLAGS=""




#TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OECONF += " \
    --exec-prefix=/usr \
    --prefix=/usr \
    --host=${HOST_SYS} \
"


do_configure() {
    # no stubs-32.h in our 64-bit sysroot - hack it into tools/include/gnu
    if ! test -f ${STAGING_DIR_TARGET}/usr/include/gnu/stubs-32.h ; then
        if test -f ${STAGING_DIR_TARGET}/usr/include/gnu/stubs-64.h ; then
            test -d ${S}/tools/include/gnu || mkdir ${S}/tools/include/gnu
            cat ${STAGING_DIR_TARGET}/usr/include/gnu/stubs-64.h | grep -v stub_bdflush | grep -v stub_getmsg | grep -v stub_putmsg > ${S}/tools/include/gnu/stubs-32.h
            echo \#define __stub___kernel_cosl >> ${S}/tools/include/gnu/stubs-32.h
            echo \#define __stub___kernel_sinl >> ${S}/tools/include/gnu/stubs-32.h
            echo \#define __stub___kernel_tanl >> ${S}/tools/include/gnu/stubs-32.h
        fi
    fi

    # do configure
    oe_runconf

}

do_compile() {
        DESTDIR=${D} oe_runmake -C tools subdir-all-include
        DESTDIR=${D} oe_runmake -C tools subdir-all-libxc
        DESTDIR=${D} oe_runmake -C tools subdir-all-flask
        DESTDIR=${D} oe_runmake -C tools subdir-all-xenstore
        DESTDIR=${D} oe_runmake -C tools subdir-all-misc
        DESTDIR=${D} oe_runmake -C tools subdir-all-hotplug
        DESTDIR=${D} oe_runmake -C tools subdir-all-xentrace
        DESTDIR=${D} oe_runmake -C tools subdir-all-xenmon
        DESTDIR=${D} oe_runmake -C tools subdir-all-console
        DESTDIR=${D} oe_runmake -C tools subdir-all-xenstat
        DESTDIR=${D} oe_runmake -C tools subdir-all-hvm-info
        DESTDIR=${D} oe_runmake -C tools subdir-all-xen-libhvm
}

do_install() {
        DESTDIR=${D} oe_runmake -C tools subdir-install-include
        DESTDIR=${D} oe_runmake -C tools subdir-install-libxc
        DESTDIR=${D} oe_runmake -C tools subdir-install-flask
        DESTDIR=${D} oe_runmake -C tools subdir-install-xenstore
        DESTDIR=${D} oe_runmake -C tools subdir-install-misc
        DESTDIR=${D} oe_runmake -C tools subdir-install-hotplug
        DESTDIR=${D} oe_runmake -C tools subdir-install-xentrace
        DESTDIR=${D} oe_runmake -C tools subdir-install-xenmon
        DESTDIR=${D} oe_runmake -C tools subdir-install-console
        DESTDIR=${D} oe_runmake -C tools subdir-install-xenstat
        DESTDIR=${D} oe_runmake -C tools subdir-install-hvm-info
        DESTDIR=${D} oe_runmake -C tools subdir-install-xen-libhvm

# Should not be necessary anymore
        rm -rf ${D}/etc/udev
        find ${D} -name "xm" -delete
        find ${D} -name "*xend*" -delete
        rm -f ${D}/usr/sbin/tapdisk ${D}/usr/sbin/blktapctrl
        rm -f ${D}/etc/xen/scripts/block

        install -d ${D}${sysconfdir}/init.d
	rm -f ${D}/etc/init.d/xencommons
	rm -f ${D}/etc/init.d/xen-watchdog
        install -m 0755 ${WORKDIR}/xenstored.initscript ${D}${sysconfdir}/init.d/xenstored
        install -m 0755 ${WORKDIR}/xenconsoled.initscript ${D}${sysconfdir}/init.d/xenconsoled
}

