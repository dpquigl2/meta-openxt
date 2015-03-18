SUMMARY = "OpenXT Shipper"

LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${TOPDIR}/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe      \
                    file://${TOPDIR}/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "\
	file://manifest \
	"

inherit openxt

INFO_DIR = "${OUT_DIR_RAW}/info"
LOGS_DIR = "${OUT_DIR}/logs"
GET_LOGS = "true"
MANIFEST_FILE = "${WORKDIR}/manifest"
REPO_DIR = "${OUT_DIR}/repository/packages.main"

do_collect_logs() {
	if [ ${GET_LOGS} ] ; then
		mkdir -p "${LOGS_DIR}"
		echo "Collecting build logs..."
		find ${TMPDIR}/work/*/*/*/temp -name "log.do_*" | tar cjf ${LOGS_DIR}/logs-${DISTRO}-${DISTRO_VERSION}.tar.bz2 --files-from=-
		echo "Done"
		echo "Collecting sigdata..."
		find ${TMPDIR}/stamps -name "*.sigdata.*" | tar cjf ${LOGS_DIR}/sigdata-${DISTRO}-${DISTRO_VERSION}.tar.bz2 --files-from=-
		echo "Done"
		echo "Collecting buildstats..."
		tar -cjf "${LOGS_DIR}/buildstats-${DISTRO}-${DISTRO_VERSION}.tar.bz2" "${TMPDIR}/buildstats"
		echo "Done"
	fi
}

repository_add() {
	local repository="$1"
	local shortname="$2"
	local format="$3"
	local required="$4"
	local filename="$5"
	local unpackdir="$6"

	cd "${REPO_DIR}"
	local filesize=$( du -b $filename | awk '{print $1}' )
	local sha256sum=$( sha256sum $filename | awk '{print $1}' )

	echo "$shortname" "$filesize" "$sha256sum" "$format" \
		"$required" "$filename" "$unpackdir" | tee -a "${REPO_DIR}/XC-PACKAGES"
}

do_repositories() {
	mkdir -p "${INFO_DIR}"

	# In case the file was already there and populated
	rm -f "${INFO_DIR}/repository"

	mkdir -p ${REPO_DIR}
	echo -n > "${REPO_DIR}/XC-PACKAGES"

	# Format of the manifest file is
	# name format optional/required source_filename dest_path
	while read l
	do
		local name=`echo "$l" | awk '{print $1}'`
		local format=`echo "$l" | awk '{print $2}'`
		local opt_req=`echo "$l" | awk '{print $3}'`
		local src=`echo "$l" | awk '{print $4}'`
		local dest=`echo "$l" | awk '{print $5}'`

		if [ ! -e "${OUT_DIR_RAW}/$src" ] ; then
			if [ "$opt_req" = "required" ] ; then
				echo "Error: Required file $src is missing"
				exit 1
			fi

			echo "Optional file $src is missing: skipping"
			break
		fi

		cp -f "${OUT_DIR_RAW}/$src" "${REPO_DIR}/$src"

		repository_add "${REPO_DIR}" "$name" "$format" "$opt_req" "$src" "$dest"
	done < "${MANIFEST_FILE}"

	PACKAGES_SHA256SUM=$(sha256sum "${REPO_DIR}/XC-PACKAGES" | awk '{print $1}')

set +o pipefail #fragile part

	# Pad XC-REPOSITORY to 1 MB with blank lines. If this is changed, the
	# repository-signing process will also need to change.
	{
            cat <<EOF
xc:main
pack:Base Pack
product:XenClient
build:${ID}
version:${VERSION}
release:${RELEASE}
upgrade-from:${UPGRADEABLE_RELEASES}
packages:${PACKAGES_SHA256SUM}
EOF
            yes ""
        } | head -c 1048576 > "${REPO_DIR}/XC-REPOSITORY"

set -o pipefail #end of fragile part
	
}

do_sign_repos() {
	local PASSPHRASE_ARG

	[ "${PASSPHRASE}" ] && PASSPHRASE_ARG="-passin env:PASSPHRASE"

	openssl smime -sign \
		-aes256 \
		-binary \
		-in "${REPO_DIR}/XC-REPOSITORY" \
		-out "${REPO_DIR}/XC-SIGNATURE" \
		-outform PEM \
		-signer "${REPO_DEV_SIGNING_CERT}" \
		-inkey "${REPO_DEV_SIGNING_KEY}" \
		${PASSPHRASE_ARG} 

	# Removed suffix variable as I cannot figure out how it ever gets set
	echo "repository: repository" >> "${INFO_DIR}/repository"
}

do_update() {
	# Removed suffix variable as I cannot figure out how it ever gets set
	local update_name="update/update.tar"

	echo "update:"
	mkdir -p "${OUT_DIR}/update"
	tar -C "${OUT_DIR}/$NAME/repository" \
		-cf "${OUT_DIR}/$update_name" packages.main

	echo "ota-update: $update_name" >> "${INFO_DIR}/update"
}

do_netboot() {
	# Just clearing suffix now as that will make alterations easier if necessary
        local suffix=""

        local path="${OUT_DIR_RAW}/installer"
        local netboot="${OUT_DIR}/netboot$suffix"
        local tarball="$path/rootfs.i686"

        echo "netboot$suffix:"

        rm -rf "$netboot"
        mkdir -p "$netboot"
        cp -f "$path/netboot/"* "$netboot"

        echo "  - extract xen.gz"
        if [ -f "$path/xen.gz" ]; then
                cp "$path/xen.gz" "$netboot/xen.gz"
        #else
		# Hardcoded to an old version of xen.  Not sure how to detect the right version.
                # get_file_from_tar_or_cpio "$tarball" "boot/xen-3.4.1-xc.gz" > "$netboot/xen.gz"
        fi
        echo "  - extract vmlinuz"
        if [ -f "$path/bzImage" ]; then
                cp "$path/bzImage" "$netboot/bzImage"
        #else
		# Not pulling over this function.  Do we really need to do this anyways?
                # get_file_from_tar_or_cpio "$tarball" "boot/vmlinuz" > "$netboot/vmlinuz"
        fi
        echo "  - extract tboot.gz"
        if [ -f "$path/tboot.gz" ]; then
                cp "$path/tboot.gz" "$netboot/tboot.gz"
        #else
                # get_file_from_tar_or_cpio "$tarball" "boot/tboot.gz" > "$netboot/tboot.gz"
        fi
        echo "  - extract ACMs"
	# Do we need to extract?  They are already extracted.
        # extract_acms "$tarball" "$path" "$netboot"
	cp -f "$path"/*.acm "$netboot"
        echo "  - copy rootfs"
        cp -f "$path/rootfs.i686.cpio.gz" "$netboot/rootfs.gz"

        echo "  - Create a tarball with netboot file"
        tar cf "$netboot/netboot.tar" -C "$netboot" .
        gzip -9 "$netboot/netboot.tar"

        echo "netboot$suffix: netboot$suffix" >> "${INFO_DIR}/netboot"

        echo ""
}

do_installer_iso() {
	# Just clearing suffix now as that will make alterations easier if necessary
	local suffix=""

	local path="${OUT_DIR_RAW}"
	local repository="${OUT_DIR}/repository$suffix"
	local iso="${OUT_DIR}/iso"
	local iso_path="$iso/installer$suffix"
	local tarball="$path/installer/rootfs.i686"
	local OPENXT_VERSION="${VERSION}"
	# Where did the build ID come from?
	# local OPENXT_BUILD_ID="$ID"
	local OPENXT_BUILD_ID="12345678"
	local OPENXT_ISO_LABEL="OpenXT-${VERSION}"
	echo "installer$suffix iso:"
	rm -rf "$iso_path" "$iso_path.iso"
	mkdir -p "$iso_path/isolinux"

	cp -f "$path/installer/iso/"* "$iso_path/isolinux"
	sed -i'' -re "s|[$]OPENXT_VERSION|$OPENXT_VERSION|g" "$iso_path/isolinux/bootmsg.txt"
	sed -i'' -re "s|[$]OPENXT_BUILD_ID|$OPENXT_BUILD_ID|g" "$iso_path/isolinux/bootmsg.txt"

	echo "  - extract xen.gz"
	if [ -f "$path/installer/xen.gz" ]; then
		cp "$path/installer/xen.gz" "$iso_path/isolinux/xen.gz"
	#else
		#get_file_from_tar_or_cpio "$tarball" "boot/xen-3.4.1-xc.gz" > "$iso_path/isolinux/xen.gz"
	fi
	echo "  - extract bzImage"
	if [ -f "$path/installer/bzImage" ]; then
		cp "$path/installer/bzImage" "$iso_path/isolinux/bzImage"
	#else
		#get_file_from_tar_or_cpio "$tarball" "boot/vmlinuz" > "$iso_path/isolinux/vmlinuz"
	fi
	echo "  - extract tboot.gz"
	if [ -f "$path/installer/tboot.gz" ]; then
		cp "$path/installer/tboot.gz" "$iso_path/isolinux/tboot.gz"
	#else
		#get_file_from_tar_or_cpio "$tarball" "boot/tboot.gz" > "$iso_path/isolinux/tboot.gz"
	fi
	echo "  - extract ACMs"
	#extract_acms "$tarball" "$path/installer/" "$iso_path/isolinux"
	cp -f "$path"/installer/*.acm "$iso_path/isolinux"
	echo "  - copy rootfs"
	cp -f "$path/installer/rootfs.i686.cpio.gz" "$iso_path/isolinux/rootfs.gz"

	cp -r "$repository/"* "$iso_path"

	echo "  - create iso"

	generate_iso "$iso_path" "$iso_path.iso" "$OPENXT_ISO_LABEL"

	rm -rf "$iso_path"

	echo "installer$suffix: iso/installer$suffix.iso" >> "${INFO_DIR}/installer"

	echo ""
}

generate_iso() {
	ISO_DIR="$1"
	ISO_IMAGE="$2"
	ISO_LABEL="$3"

	genisoimage -o "${ISO_IMAGE}" \
		-b "isolinux/isolinux.bin" \
		-c "isolinux/boot.cat" \
		-no-emul-boot \
		-boot-load-size 4 \
		-boot-info-table \
		-r \
		-J \
		-V "${ISO_LABEL}" \
		-quiet \
		"${ISO_DIR}"

	"${ISO_DIR}/isolinux/isohybrid" "${ISO_IMAGE}"
}


addtask do_collect_logs after do_unpack before do_repositories
addtask do_repositories after do_collect_logs before do_sign_repos
addtask do_sign_repos after do_repositories before do_update
addtask do_update after do_sign_repos before do_populate_lic
addtask do_netboot after do_sign_repos before do_populate_lic
addtask do_installer_iso after do_sign_repos before do_populate_lic
