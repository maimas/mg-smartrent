package com.mg.samartrent.user.integration.bootstrap

import com.mg.samartrent.user.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.enums.EnGender
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest(classes = UserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestServiceBootstrap extends IntegrationTestsSetup {

    @Autowired
    private UserService userService

    @Autowired
    private PasswordEncoder passwordEncoder

    def "test: admin user creation on system startup"() {
        when:
        def admin = userService.findByEmail("sys.admin@smartrent.com")

        then:
        admin != null
        admin.getTrackingId() == '12341234'
        admin.getCreatedDate() != null
        admin.getModifiedDate() != null
        admin.getFirstName() == "System"
        admin.getLastName() == "Administrator"
        admin.getEmail() == "sys.admin@smartrent.com"
        admin.getStatus() == EnUserStatus.Active.name()
        admin.getGender() == EnGender.Unknown.name()
        admin.getDateOfBirth() != null
        admin.getPassword() != "12341234"
        passwordEncoder.matches("12341234", admin.getPassword())

    }


}
