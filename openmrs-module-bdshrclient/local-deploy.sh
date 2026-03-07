# This Source Code Form is subject to the terms of the Mozilla Public License,
# v. 2.0. If a copy of the MPL was not distributed with this file, You can
# obtain one at https://www.bahmni.org/license/mplv2hd.
#
# Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
# graphic logo is a trademark of OpenMRS Inc.

#!/bin/sh
mvn clean package -DskipTests
scp -i ~/.ssh/id_rsa shrclient-omod/target/shrclient-2.0-SNAPSHOT.omod bahmni@192.168.33.18:~/.OpenMRS/modules
scp -i ~/.ssh/id_rsa shrclient-omod/src/main/resources/*.properties bahmni@192.168.33.18:~/.OpenMRS/
