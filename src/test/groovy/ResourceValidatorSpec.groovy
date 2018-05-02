import com.fasterxml.jackson.databind.ObjectMapper
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
        final ObjectMapper mapper = new ObjectMapper();

        then:
        try {
            def file = new File(RESOURCES_PATH + resourceName)
            mapper.readTree(file)
        } catch (Exception e) {
            throw new RuntimeException("resource '${resourceName}' should be valid json", e)
        }

        where:
        resourceName << resourceNames
    }
}
