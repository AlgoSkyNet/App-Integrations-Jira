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

package org.symphonyoss.integration.jira.webhook.parser;

import static org.symphonyoss.integration.jira.webhook.JiraEventConstants.ISSUE_EVENT_TYPE_NAME;
import static org.symphonyoss.integration.jira.webhook.JiraEventConstants.WEBHOOK_EVENT;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.parser.WebHookParserFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * Common methods to retrieve the parser according to received event.
 * Created by rsanchez on 31/03/17.
 */
public abstract class JiraParserFactory implements WebHookParserFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(JiraParserFactory.class);

  protected Map<String, JiraParser> parsers = new HashMap<>();

  @Autowired
  private NullJiraParser defaultJiraParser;

  /**
   * Map the event type to the parser.
   */
  @PostConstruct
  public void init() {
    for (JiraParser parser : getBeans()) {
      List<String> events = parser.getEvents();
      for (String eventType : events) {
        this.parsers.put(eventType, parser);
      }
    }
  }

  /**
   * Update the integration username on each parser class. This process is required to know which user
   * must be used to query the Symphony API's.
   * @param settings Integration settings
   */
  @Override
  public void onConfigChange(IntegrationSettings settings) {
    String jiraUser = settings.getType();

    for (JiraParser parser : getBeans()) {
      parser.setIntegrationUser(jiraUser);
    }
  }

  @Override
  public WebHookParser getParser(WebHookPayload payload) {
    try {
      JsonNode rootNode = JsonUtils.readTree(payload.getBody());
      JiraParser parser = getParser(rootNode);

      if (parser == null) {
        parser = defaultJiraParser;
      }

      return new JiraWebHookParserAdapter(parser);
    } catch (IOException e) {
      throw new JiraParserException("Cannot retrieve the payload event", e);
    }
  }

  /**
   * Get the parser class based on the event received from JIRA.
   *
   * The fields used to do perform this selection are 'webhookEvent' and 'issue_event_type_name'.
   *
   * @param node JIRA event
   * @return Parser class to handle the event
   */
  public JiraParser getParser(JsonNode node) {
    String webHookEvent = node.path(WEBHOOK_EVENT).asText();
    String eventTypeName = node.path(ISSUE_EVENT_TYPE_NAME).asText();

    JiraParser result = parsers.get(eventTypeName);

    if (result == null) {
      result = parsers.get(webHookEvent);
    }

    if (result == null) {
      LOGGER.debug("Unhandled event {}", webHookEvent);
    }

    return result;
  }

  /**
   * Get a list of parsers supported by the factory.
   * @return list of parsers supported by the factory.
   */
  protected abstract List<JiraParser> getBeans();

}
