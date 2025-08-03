package customer.service.impl;

import customer.service.PermissionService;

import java.util.ArrayList;
import java.util.List;

public class PermissionServiceImpl implements PermissionService {
    public List<String> getUserPermissions(Integer userId) {
        //return new ArrayList<String>();
        System.out.println("PermissionServiceImpl");
        return List.of("p1", "p2");
    }
}
