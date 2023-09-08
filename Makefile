# Copyright (c) 2022-present Doodle. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
CONFIG ?= DOODLE
DOODLE_MODULES ?= $(file < $(CONFIG))
DOODLE_GIT ?= git@github.com:org-doodle
RM=rm -rf

.PHONY: all
all : $(DOODLE_MODULES)

.PHONY: update $(addprefix update-,$(DOODLE_MODULES))
update : $(addprefix update-,$(DOODLE_MODULES))

.PHONY: install $(addprefix install-,$(DOODLE_MODULES))
install: $(addprefix install-,$(DOODLE_MODULES))

.PHONY: clean $(addprefix clean-,$(DOODLE_MODULES))
clean: $(addprefix clean-,$(DOODLE_MODULES))

is-not-installed=! (test -d $(1))

define install-module
$(1):
	@if ($(call is-not-installed,$(1))) ; \
	then \
		echo "没有找到 [" $(1) "] 模块"; \
		echo "开始安装 [" $(1) "] 模块"; \
		git clone $(DOODLE_GIT)/$(1) ; \
	else \
		echo "本地存在 [" $(1) ]" 模块"; \
	fi
update-$(1): $(1)
	@echo "更新 [" $(1) "] 模块"
	cd $(1) && git pull origin main
install-$(1): $(1)
	@echo "安装 [" $(1) "] 模块"
	cd $(1) && ./mvnw source:jar install
clean-$(1):
	@echo "清理 [" $(1) "] 模块"
	@$(RM) $(1)
endef

$(foreach module,$(DOODLE_MODULES), $(eval $(call install-module,$(module))))
