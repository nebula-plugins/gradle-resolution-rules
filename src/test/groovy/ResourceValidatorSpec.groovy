
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class ResourceValidatorSpec extends Specification {
    static final String RESOURCES_PATH = 'src' + File.separator + 'main' + File.separator + 'resources' + File.separator
    @Shared
            resourceNames

    void setupSpec() {
        def resourcesFolder = new File(RESOURCES_PATH)
        resourceNames = resourcesFolder.listFiles().collect { file ->
            file.name
        }
    }

    @Unroll
    def 'verifies resource \'#resourceName\' is valid json'() {
        when:
        try {
            def file = new File(RESOURCES_PATH + resourceName)
            new JsonSlurper().parse(file)
        } catch (Exception e) {
            throw new RuntimeException("resource '${resourceName}' should be valid json", e)
        }

        then:
        noExceptionThrown()

        where:
        resourceName << resourceNames
    }
}
