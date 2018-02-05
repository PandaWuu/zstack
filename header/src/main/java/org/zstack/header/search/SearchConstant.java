package org.zstack.header.search;

public interface SearchConstant {
    public static final String INDEX_MANAGER_SERVICE_ID = "InventoryIndexManager";
    public static final String QUERY_FACADE_SERVICE_ID = "query";
    String ACTION_CATEGORY = "query";

    public static enum SearchGlobalConfig {
        DefaultSearchSize;

        public String getCategory() {
            return "Search";
        }
    }
}
