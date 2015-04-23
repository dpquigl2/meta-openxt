FILESEXTRAPATHS_append := ":${THISDIR}/${PN}-${PV}"
DEPENDS += "libbudgetvhd"
SRC_URI += "file://autofix-sb-future-timestamps.patch"
SRC_URI += "file://fix-infinite-recursion.patch"
SRC_URI += "file://vhd.patch"

#do_configure_append() {
#    autoconf
#}

