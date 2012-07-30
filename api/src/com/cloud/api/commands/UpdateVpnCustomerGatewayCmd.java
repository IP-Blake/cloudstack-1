// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.api.commands;

import org.apache.log4j.Logger;

import com.cloud.api.ApiConstants;
import com.cloud.api.BaseAsyncCmd;
import com.cloud.api.BaseCmd;
import com.cloud.api.IdentityMapper;
import com.cloud.api.Implementation;
import com.cloud.api.Parameter;
import com.cloud.api.ServerApiException;
import com.cloud.api.response.Site2SiteCustomerGatewayResponse;
import com.cloud.event.EventTypes;
import com.cloud.network.Site2SiteCustomerGateway;
import com.cloud.user.Account;
import com.cloud.user.UserContext;

@Implementation(description="Update site to site vpn customer gateway", responseObject=Site2SiteCustomerGatewayResponse.class)
public class UpdateVpnCustomerGatewayCmd extends BaseAsyncCmd {
    public static final Logger s_logger = Logger.getLogger(UpdateVpnCustomerGatewayCmd.class.getName());

    private static final String s_name = "updatecustomergatewayresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////
    @IdentityMapper(entityTableName="s2s_customer_gateway")
    @Parameter(name=ApiConstants.ID, type=CommandType.LONG, required=true, description="id of customer gateway")
    private Long id;
    
    @Parameter(name=ApiConstants.GATEWAY, type=CommandType.STRING, required=true, description="public ip address id of the customer gateway")
    private String gatewayIp;

    @Parameter(name=ApiConstants.CIDR_LIST, type=CommandType.STRING, required=true, description="guest cidr of the customer gateway")
    private String guestCidrList;

    @Parameter(name=ApiConstants.IPSEC_PSK, type=CommandType.STRING, required=true, description="IPsec Preshared-Key of the customer gateway")
    private String ipsecPsk;

    @Parameter(name=ApiConstants.IKE_POLICY, type=CommandType.STRING, required=true, description="IKE policy of the customer gateway")
    private String ikePolicy;

    @Parameter(name=ApiConstants.ESP_POLICY, type=CommandType.STRING, required=true, description="ESP policy of the customer gateway")
    private String espPolicy;

    @Parameter(name=ApiConstants.LIFETIME, type=CommandType.LONG, required=false, description="Lifetime of vpn connection to the customer gateway, in seconds")
    private Long lifetime;

    @Parameter(name=ApiConstants.ACCOUNT, type=CommandType.STRING, description="the account associated with the gateway. Must be used with the domainId parameter.")
    private String accountName;
    
    @IdentityMapper(entityTableName="domain")
    @Parameter(name=ApiConstants.DOMAIN_ID, type=CommandType.LONG, description="the domain ID associated with the gateway. " +
    		"If used with the account parameter returns the gateway associated with the account for the specified domain.")
    private Long domainId;
    
    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public String getEntityTable() {
    	return "s2s_customer_gateway";
    }
    
    public Long getId() {
        return id;
    }
    
    public String getIpsecPsk() {
        return ipsecPsk;
    }

    public String getGuestCidrList() {
        return guestCidrList;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public String getIkePolicy() {
        return ikePolicy;
    }

    public String getEspPolicy() {
        return espPolicy;
    }

    public Long getLifetime() {
        return lifetime;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////


    @Override
    public String getCommandName() {
        return s_name;
    }

	@Override
	public long getEntityOwnerId() {
        Long accountId = finalyzeAccountId(accountName, domainId, null, true);
        if (accountId == null) {
            accountId = UserContext.current().getCaller().getId();
        }
        return accountId;
    }

	@Override
	public String getEventDescription() {
		return "Update site-to-site VPN customer gateway";
	}

	@Override
	public String getEventType() {
		return EventTypes.EVENT_S2S_VPN_CUSTOMER_GATEWAY_UPDATE;
	}
	
    @Override
    public void execute(){
        Site2SiteCustomerGateway result = _s2sVpnService.updateCustomerGateway(this);
        if (result != null) {
            Site2SiteCustomerGatewayResponse response = _responseGenerator.createSite2SiteCustomerGatewayResponse(result);
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(BaseCmd.INTERNAL_ERROR, "Failed to update customer VPN gateway");
        }
    }
}
