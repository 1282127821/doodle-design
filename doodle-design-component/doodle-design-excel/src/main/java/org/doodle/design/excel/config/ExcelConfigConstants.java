/*
 * Copyright (c) 2022-present Doodle. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.doodle.design.excel.config;

import lombok.experimental.UtilityClass;

/** Excel 配置服务常量 */
@UtilityClass
public final class ExcelConfigConstants {

  /**
   * doodleExcel:config?xlsx?=bag&sheet=base
   * doodleExcel:config?xlsx?=bag&sheet=base&loadingMode=local
   * doodleExcel:config?xlsx?=bag&sheet=base&loadingMode=remote
   */
  public static final String PREFIX = "doodleExcel:";

  /** loadingMode tag */
  public static final String LOADING_MODE = "loadingMode";

  /** xlsx tag */
  public static final String XLSX = "xlsx";

  /** sheet tag */
  public static final String SHEET = "sheet";
}
