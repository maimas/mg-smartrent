//package com.mg.samartrent.renter.integration.resources
//
//import com.mg.apps.maicaller.rest.SysMetadataRestController
//import com.mg.persistence.domain.SystemFiled
//import com.mg.persistence.domain.bizitem.model.BizItemModel
//import com.mg.persistence.services.QueryService
//import org.codehaus.jackson.map.ObjectMapper
//import org.codehaus.jackson.type.TypeReference
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.mongodb.core.query.Criteria
//import org.springframework.data.mongodb.core.query.Query
//import org.springframework.http.MediaType
//import org.springframework.test.context.TestPropertySource
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.MvcResult
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
//import spock.lang.Specification
//
//class SetupTests extends Specification {
//
//    protected static boolean setupExecuted = false
//    protected static MockMvc mockMvc
//
//
//    @Autowired
//    protected QueryService queryService
//
//    @Autowired
//    private SysMetadataRestController metadataRestController
//
//    def setup() {
//        if (!setupExecuted) {
//            "---> SETTING UP THE TESTS..."
//            metadataRestController.init()
//            setupExecuted = true
//        }
//    }
//
//
//    def purgeCollection(String itemType) {
//        println("==================PURGING $itemType=========================")
//        Query q = new Query().addCriteria(Criteria.where(SystemFiled.ID).exists(true))
//        queryService.delete(q, itemType)
//        def find = queryService.find(q, itemType, BizItemModel.class)
//        println find.size()
//    }
//
//
//    MvcResult doPost(MockMvc mockMvc, String restUri, BizItemModel model) {
//        return mockMvc
//                .perform(MockMvcRequestBuilders.post(restUri)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(model)))
//                .andReturn()
//    }
//
//    MvcResult doPost(MockMvc mockMvc, String restUri, List<BizItemModel> models) {
//        return mockMvc
//                .perform(MockMvcRequestBuilders.post(restUri)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(models)))
//                .andReturn()
//    }
//
//
//    MvcResult doGet(MockMvc mockMvc, String restUri) {
//        return mockMvc.perform(MockMvcRequestBuilders.get(restUri)).andReturn()
//    }
//
//
//    BizItemModel mvcResultToModel(MvcResult mvcResult) {
//        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BizItemModel.class)
//    }
//
//
//    List<BizItemModel> mvcResultToModels(MvcResult mvcResult) {
//        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<BizItemModel>>() {
//        })
//    }
//
//
//}
