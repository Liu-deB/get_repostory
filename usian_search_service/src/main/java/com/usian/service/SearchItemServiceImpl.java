package com.usian.service;
import com.github.pagehelper.PageHelper;
import com.usian.mapper.SearchItemMapper;
import com.usian.pojo.SearchItem;
import com.usian.pojo.TbItemExample;
import com.usian.utils.JsonUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SearchItemServiceImpl{

    @Autowired
    private SearchItemMapper searchItemMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${ES_INDEX_NAME}")
    private String ES_INDEX_NAME;

    @Value("${ES_TYPE_NAME}")
    private String ES_TYPE_NAME;

    public Boolean importAll() {
        try {
            if(!isExistsIndex()){
                createIndex();
            }
            int page=1;
            while (true){
                /**???????????????????????????*/
                PageHelper.startPage(page,1000);
                //1?????????mysql??????????????????
                List<SearchItem> esDocumentList = searchItemMapper.getItemList();
                if(esDocumentList==null || esDocumentList.size()==0){
                    break;
                }
                BulkRequest bulkRequest = new BulkRequest();
                for (int i = 0; i < esDocumentList.size(); i++) {
                    SearchItem searchItem =  esDocumentList.get(i);
                    //2???????????????????????????es???
                    bulkRequest.add(new IndexRequest(ES_INDEX_NAME, ES_TYPE_NAME).
                        source(JsonUtils.objectToJson(searchItem), XContentType.JSON));
                }
                restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                page++;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ?????????????????????
     * @return
     * @throws IOException
     */
    private boolean isExistsIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(ES_INDEX_NAME);
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * ???????????????
     * @return
     * @throws IOException
     */
    private boolean createIndex() throws IOException {
        //????????????????????????????????????????????????
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(ES_INDEX_NAME);
        //??????????????????
        createIndexRequest.settings(Settings.builder().put("number_of_shards",2)
                                    				.put("number_of_replicas",1));
        createIndexRequest.mapping(ES_TYPE_NAME, "{\n" +
                "  \"_source\": {\n" +
                "    \"excludes\": [\n" +
                "      \"item_desc\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"properties\": {\n" +
                "    \"item_title\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_sell_point\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_price\": {\n" +
                "      \"type\": \"float\"\n" +
                "    },\n" +
                "    \"item_image\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"index\": false\n" +
                "    },\n" +
                "    \"item_category_name\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"item_desc\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    }\n" +
                "  }\n" +
                "}", XContentType.JSON);
        //???????????????????????????
        IndicesClient indices = restHighLevelClient.indices();

        //??????????????????
        CreateIndexResponse createIndexResponse = 
            indices.create(createIndexRequest,RequestOptions.DEFAULT);
        //??????????????????
        return createIndexResponse.isAcknowledged();
    }

    public List<SearchItem> selectByQ(String q, Long page, Integer pageSize) {
        try{
            SearchRequest searchRequest = new SearchRequest(ES_INDEX_NAME);
            searchRequest.types(ES_TYPE_NAME);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //1???????????????????????????????????????????????????q????????????
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(q,new String[]{
                    "item_title","item_desc","item_sell_point","item_category_name"}));
            //2?????????
            /**
             * 1  0  20--->(p-1)*pageSize
             * 2  20 20--->(2-1)*20
             * 3  40 20--->(3-1)*20
             */
            Long  from = (page - 1) * pageSize;
            searchSourceBuilder.from(from.intValue());
            searchSourceBuilder.size(pageSize);
            //3?????????
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<font color='red'>");
            highlightBuilder.postTags("</font>");
            highlightBuilder.field("item_title");
            searchSourceBuilder.highlighter(highlightBuilder);

            searchRequest.source(searchSourceBuilder);
            SearchResponse response = restHighLevelClient.search(
                    searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            //4?????????????????????
            List<SearchItem> searchItemList = new ArrayList<>();
            for (int i=0; i<hits.length; i++){
                SearchHit hit = hits[i];
                SearchItem searchItem = JsonUtils.jsonToPojo(hit.getSourceAsString(),
                        SearchItem.class);
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if(highlightFields!=null && highlightFields.size()>0) {
                    searchItem.setItem_title(highlightFields.get("item_title").
                            getFragments()[0].toString());
                }
                searchItemList.add(searchItem);
            }
            return searchItemList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int insertDocument(String itemId) throws IOException {
        // 1???????????????id?????????????????????
        SearchItem searchItem = searchItemMapper.getItemById(Long.valueOf(itemId));

        //2???????????????????????????
        IndexRequest indexRequest = new IndexRequest(ES_INDEX_NAME, ES_TYPE_NAME);
        indexRequest.source(JsonUtils.objectToJson(searchItem), XContentType.JSON);
        IndexResponse indexResponse =
                restHighLevelClient.index(indexRequest,RequestOptions.DEFAULT);
        return indexResponse.getShardInfo().getFailed();
    }
}