/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.webhook.jira.parser.v2.model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Metadata field used to filter an array of objects according to the specific value (tag <equals>) in the path.
 * Created by rsanchez on 31/03/17.
 */
public class FilterCondition {

  private String arrayPath;

  private String field;

  private String equals;

  @XmlAttribute
  public String getArrayPath() {
    return arrayPath;
  }

  public void setArrayPath(String arrayPath) {
    this.arrayPath = arrayPath;
  }

  @XmlAttribute
  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  @XmlAttribute
  public String getEquals() {
    return equals;
  }

  public void setEquals(String equals) {
    this.equals = equals;
  }
}
