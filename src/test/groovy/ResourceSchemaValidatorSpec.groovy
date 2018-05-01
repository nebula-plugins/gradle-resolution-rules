import org.everit.json.schema.Schema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class ResourceSchemaValidatorSpec extends Specification {
    static final String RESOURCES_PATH = 'src' + File.separator + 'main' + File.separator + 'resources' + File.separator
    static
    final String TEST_RESOURCES_PATH = 'src' + File.separator + 'test' + File.separator + 'resources' + File.separator
    @Shared
            resourceNames
    @Shared
            testResourceNames

    void setupSpec() {
        def resourcesFolder = new File(RESOURCES_PATH)
        resourceNames = resourcesFolder.listFiles().collect { file ->
            file.name
        }

        def testResourcesFolder = new File(TEST_RESOURCES_PATH + 'rules' + File.separator)
        testResourceNames = testResourcesFolder.listFiles().collect { file ->
            file.name
        }
    }

    @Unroll
    def 'verifies resource \'#resourceName\' matches schema'() {
        when:
        def schemaName = 'valid-schema.json'

        then:
        Schema schema
        try {
            def schemaInputStream = getClass().getClassLoader().getResourceAsStream(schemaName)
            JSONObject rawSchema = new JSONObject(new JSONTokener(schemaInputStream))
            schema = SchemaLoader.load(rawSchema)
        } catch (Exception e) {
            throw new RuntimeException("schema at '${TEST_RESOURCES_PATH + schemaName}' has a problem", e)
        }

        JSONObject rawResource
        try {
            def file = new File(RESOURCES_PATH + resourceName)
            def resourceInputStream = new FileInputStream(file)
            rawResource = new JSONObject(new JSONTokener(resourceInputStream))
        } catch (Exception e) {
            throw new RuntimeException("resource '${resourceName}' should be valid json", e)
        }

        try {
            schema.validate(rawResource)
        } catch (ValidationException e) {
            def output = [e.getMessage()]
            e.getCausingExceptions().stream()
                    .collect { exception -> exception.getMessage() }
                    .forEach { message -> output.add(message) }

            throw new RuntimeException("\nResource ${resourceName} failed validation.\n\n" +
                    String.join('\n', output))
        }

        where:
        resourceName << resourceNames
    }

    @Unroll
    def 'verifies invalid resource \'#resourceName\' is does not match schema'() {
        when:
        def schemaName = 'valid-schema.json'

        then:
        Schema schema
        try {
            def schemaInputStream = getClass().getClassLoader().getResourceAsStream(schemaName)
            JSONObject rawSchema = new JSONObject(new JSONTokener(schemaInputStream))
            schema = SchemaLoader.load(rawSchema)
        } catch (Exception e) {
            throw new RuntimeException("schema at '${TEST_RESOURCES_PATH + schemaName}' has a problem", e)
        }

        when:
        JSONObject rawResource
        try {
            def file = new File(TEST_RESOURCES_PATH + 'rules' + File.separator + resourceName)
            def resourceInputStream = new FileInputStream(file)
            rawResource = new JSONObject(new JSONTokener(resourceInputStream))
        } catch (Exception e) {
            throw new RuntimeException("resource '${resourceName}' should be valid json", e)
        }

        schema.validate(rawResource)

        then:
        thrown RuntimeException

        where:
        resourceName << testResourceNames
    }
}
