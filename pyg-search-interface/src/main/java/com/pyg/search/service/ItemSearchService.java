package com.pyg.search.service;

import java.util.List;
import java.util.Map;


public interface ItemSearchService {
    /**
     * 搜索方法
     * @param searchMap
     * @return
     */
    public Map search(Map searchMap);

    /**
     * 导入列表数据solr
     * @param list
     */
    public void importList(List list);

    /**
     * 删除solr中的商品列表
     * @param goodsIds
     */
    public void deleteByGoodsIds(List goodsIds);
}
