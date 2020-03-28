package com.mnt.sampark.modules.permission.dto;

import java.util.ArrayList;
import java.util.List;

public class PermissionHierarchy {
	public Long id;
	public String label;
	public String name;
	public String permission;
	public boolean leaf = false;
	public boolean selected = false;
	public List<PermissionHierarchy> children = new ArrayList<>();

}
