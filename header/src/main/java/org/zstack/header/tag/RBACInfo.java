package org.zstack.header.tag;

import org.zstack.header.identity.rbac.RBACDescription;
import org.zstack.header.vo.ResourceVO;

public class RBACInfo implements RBACDescription {
    @Override
    public void permissions() {
        permissionBuilder()
                .normalAPIs("org.zstack.header.tag.**")
                .name("tag")
                .targetResources(ResourceVO.class)
                .build();
    }

    @Override
    public void contributeToRoles() {
        roleContributorBuilder()
                .roleName("other")
                .actionsByPermissionName("tag")
                .build();
    }

    @Override
    public void roles() {

    }

    @Override
    public void globalReadableResources() {

    }
}
