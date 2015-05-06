SUMMARY = "Haskell GHC6 Cross Compiler"
DESCRIPTION = "Haskell is an advanced purely-functional programming language. An open-source \
               product of more than twenty years of cutting-edge research, it allows rapid \ 
               development of robust, concise, correct software. With strong support for \
               integration with other languages, built-in concurrency and parallelism, \
               debuggers, profilers, rich libraries and an active community, Haskell makes it \
               easier to produce flexible, maintainable, high-quality software."
AUTHOR = "Adam Oliver <aikidokatech@users.noreply.github.com>"
HOMEPAGE = "http://www.haskell.org/"
SECTION = "devel"
LICENSE = "Glascow"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7cb08deb79c4385547f57d6bb2864e0f"

DEPENDS = "virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}binutils ncurses"

FILESEXTRAPATHS_prepend := "${THISDIR}/ghc-${PV}:"

SRC_URI = " \
            http://downloads.haskell.org/~ghc/${PV}/ghc-${PV}-src.tar.bz2 \
            file://oe-unknown-vendor-autoconf.patch \
            file://configure-site.post-configure-patch \
            file://build.mk \
            file://ghcautoconf.h \
"

SRC_URI[md5sum] = "f4b8c79c356be70a1b340e19c24a01c1"
SRC_URI[sha256sum] = "59e3bd514a1820cc1c03e1808282205c0b8518369acae12645ceaf839e6f114b"

S = "${WORKDIR}/ghc-${PV}"

inherit autotools-brokensep pkgconfig cross pythonnative

EXTRA_OECONF += " --with-gcc=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}gcc --with-ld=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}ld --with-nm=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}nm --with-objdump=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}objdump --with-cpp=${STAGING_BINDIR_TOOLCHAIN}${TARGET_PREFIX}cpp --enable-shared"

do_configure_prepend() {
    cp `which pwd` utils/ghc-pwd/ghc-pwd
    export CPP=`which cpp`
}

do_configure() {
    # Autoconf only detects cross compilation if build != host.  In our case build == host and it
    # is target that is different.
    
    # Regenerate configure in case configure.ac or aclocal.m4 got patched after unpacking.  Probably
    # best to instead regenerate configure when creating the patch, in order to generate a configure
    # patch for the recipe.
    autoconf -v -f

    # Autoconf will set the configure script to pull in some predefined preprocessor values.
    # This is bad since autoconf won't realize we are actually cross compiling.
    patch -p1 < ${WORKDIR}/configure-site.post-configure-patch

    # Run the resulting configure script.
    oe_runconf
}

do_configure_append() {
    if [ ! -e ${S}/mk/build.mk ]; then
        cp -f ${WORKDIR}/build.mk ${S}/mk/build.mk
    fi

    # look there for bfd.h stupid cow:
    #echo "STANDARD_OPTS += \"-I${STAGING_INCDIR_NATIVE}\"" >> rts/ghc.mk

    # Copy target platform includes now that we are cross compiling.
    cp -f ${WORKDIR}/ghcautoconf.h ${S}/includes/ghcautoconf.h
}

do_compile() {
    oe_runmake
}

do_install_append() {
    #ln -sf ${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}runghc-ghc-${PV}/bin/runghc "${D}/${STAGING_BINDIR_TOOLCHAIN}/runghc"
}
