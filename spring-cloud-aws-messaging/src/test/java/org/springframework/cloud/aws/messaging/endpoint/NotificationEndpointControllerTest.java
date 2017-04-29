/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.aws.messaging.endpoint;

import com.amazonaws.services.sns.AmazonSNS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Agim Emruli
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration
public class NotificationEndpointControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AmazonSNS amazonSnsMock;

    @Autowired
    private NotificationTestController notificationTestController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void subscribe_subscriptionConfirmationRequestReceived_subscriptionConfirmedThroughSubscriptionStatus() throws Exception {
        //Arrange
        byte[] subscriptionRequestJsonContent = FileCopyUtils.copyToByteArray(new ClassPathResource("subscriptionConfirmation.json", getClass()).getInputStream());

        //Act
        this.mockMvc.perform(post("/mySampleTopic").header("x-amz-sns-message-type", "SubscriptionConfirmation").content(subscriptionRequestJsonContent)).andExpect(status().isNoContent());

        //Assert
        verify(this.amazonSnsMock, times(1)).confirmSubscription("arn:aws:sns:eu-west-1:111111111111:mySampleTopic", "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
    }

    @Test
    public void notification_notificationReceivedAsMessage_notificationSubjectAndMessagePassedToAnnotatedControllerMethod() throws Exception {
        //Arrange
        byte[] notificationJsonContent = FileCopyUtils.copyToByteArray(new ClassPathResource("notificationMessage.json", getClass()).getInputStream());

        //Act
        this.mockMvc.perform(post("/mySampleTopic").header("x-amz-sns-message-type", "Notification").content(notificationJsonContent)).andExpect(status().isNoContent());

        //Assert
        assertEquals("asdasd", this.notificationTestController.getMessage());
        assertEquals("asdasd", this.notificationTestController.getSubject());
    }

    @Test
    public void notification_unsubscribeConfirmationReceivedAsMessage_reSubscriptionCalledByController() throws Exception {
        //Arrange
        byte[] notificationJsonContent = FileCopyUtils.copyToByteArray(new ClassPathResource("unsubscribeConfirmation.json", getClass()).getInputStream());

        //Act
        this.mockMvc.perform(post("/mySampleTopic").header("x-amz-sns-message-type", "UnsubscribeConfirmation").content(notificationJsonContent)).andExpect(status().isNoContent());

        //Assert
        verify(this.amazonSnsMock, times(1)).confirmSubscription("arn:aws:sns:eu-west-1:111111111111:mySampleTopic", "2336412f37fb687f5d51e6e241d638b05824e9e2f6713b42abaeb8607743f5ba91d34edd2b9dabe2f1616ed77c0f8801ee79911d34dca3d210c228af87bd5d9597bf0d6093a1464e03af6650e992ecf54605e020f04ad3d47796045c9f24d902e72e811a1ad59852cad453f40bddfb45");
    }
}
