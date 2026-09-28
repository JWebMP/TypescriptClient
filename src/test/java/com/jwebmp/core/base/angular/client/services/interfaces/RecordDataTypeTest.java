package com.jwebmp.core.base.angular.client.services.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDataType;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RecordDataTypeTest
{
    @NgDataType
    public record Profile(@NotNull String name, int age) implements INgDataType<Profile> {}
    @NgDataType
    public record SystemData(String id) implements INgDataType<SystemData> {}
    @NgDataType
    public record ProfileData(String profileId, Map<String, String> attributes) implements INgDataType<ProfileData> {}
    @NgDataType
    public record SessionData(String id, Map<String, String> values) implements INgDataType<SessionData> {}
    @NgDataType
    public record OptionalData(Optional<ProfileData> profile) implements INgDataType<OptionalData> {}
    @NgDataType
    public record GenericData<T>(T value) implements INgDataType<GenericData<T>> {}
    @NgDataType
    public record GenericHolder(List<GenericData<String>> values) implements INgDataType<GenericHolder> {}
    @NgDataType(NgDataType.DataTypeClass.Class)
    public record RecursiveData(String name, RecursiveData next) implements INgDataType<RecursiveData> {}

    @NgDataType(NgDataType.DataTypeClass.Class)
    public record ClassProfile(String name, int age, Profile profile, List<Profile> history)
            implements INgDataType<ClassProfile> {}

    // Matches the data-bearing fields of ne1-world web-shell AccountDetails.
    @NgDataType
    public record AccountDetails(String accountName, String accessLevel, String bindingId,
                          String partyId, String enterpriseId, String enterpriseName,
                          String issuer, String subject, long expiresAt, long renewalExpiresAt,
                          String sessionExpires, List<String> roles, List<SystemData> systems,
                          ProfileData profile, SessionData session, Profile[] sessions, @JsonIgnore String secret)
            implements INgDataType<AccountDetails> {}

    @Test
    void rendersRecordComponentsInOrderWithGenericAndArrayTypes()
    {
        var record = new AccountDetails("", "", "", "", "", "", "", "", 0, 0, "", List.of(), List.of(), null, null, new Profile[0], "");
        String fields = String.join("", record.fields());
        assertTrue(fields.indexOf("accountName?") < fields.indexOf("accessLevel?"), fields);
        assertTrue(fields.contains("expiresAt : number"), fields);
        assertTrue(fields.contains("roles? : string[]"), fields);
        assertTrue(fields.contains("systems? : SystemData[]"), fields);
        assertTrue(fields.contains("session? : SessionData"), fields);
        assertTrue(fields.contains("sessions? : Profile[]"), fields);
        assertTrue(fields.contains("profile? : ProfileData"), fields);
        assertFalse(fields.contains("secret"), fields);
        assertTrue(record.getComponentReferences().stream().anyMatch(ref -> ref.value() == Profile.class));
        assertTrue(record.getComponentReferences().stream().anyMatch(ref -> ref.value() == ProfileData.class));
        assertTrue(record.getComponentReferences().stream().anyMatch(ref -> ref.value() == SystemData.class));
    }

    @Test
    void rendersInterfaceAndClassModes()
    {
        var profile = new Profile("Ada", 3);
        String interfaceFields = String.join("", profile.fields());
        assertTrue(interfaceFields.contains("name : string;"), interfaceFields);
        assertTrue(interfaceFields.contains("age : number;"), interfaceFields);
        assertFalse(interfaceFields.contains("public"), interfaceFields);
        assertTrue(INgDataType.renderObjectStructure(Profile.class).toString().contains("name: ''"));
        String classFields = String.join("", new ClassProfile("", 0, null, List.of()).fields());
        assertTrue(classFields.contains("public name? : string = '';"), classFields);
        assertTrue(classFields.contains("public profile? : Profile = {name: '',age: 0};"), classFields);
        assertTrue(classFields.contains("public history? : Profile[] = [];"), classFields);
        assertTrue(String.join("", new ProfileData("", Map.of()).fields()).contains("attributes? : Record<string, string>"));
        assertTrue(String.join("", new OptionalData(Optional.empty()).fields()).contains("profile? : ProfileData | null"));
        assertTrue(String.join("", new GenericHolder(List.of()).fields()).contains("values? : GenericData<string>[]"));
        assertTrue(String.join("", new RecursiveData("", null).fields()).contains("next? : RecursiveData"));
    }

    @Test
    void serializesRecordComponentsAsJson()
    {
        String json = new Profile("Ada", 3).toJson(true);
        assertTrue(json.contains("\"name\":\"Ada\""), json);
        assertTrue(json.contains("\"age\":3"), json);
        String accountJson = new AccountDetails("Ada", "", "", "", "", "", "", "", 1, 2, "",
                List.of("reader"), List.of(new SystemData("sys")), new ProfileData("profile", Map.of("tier", "one")),
                new SessionData("session", Map.of("ip", "local")), new Profile[]{new Profile("Bob", 4)}, "hidden").toJson(true);
        assertTrue(accountJson.contains("\"systems\":[{\"id\":\"sys\"}]"), accountJson);
        assertTrue(accountJson.contains("\"sessions\":[{\"name\":\"Bob\",\"age\":4}]"), accountJson);
        assertFalse(accountJson.contains("hidden"), accountJson);
        Profile original = new Profile("Ada", 3);
        Profile restored = original.fromJson(original.toJson(true));
        assertEquals(original, restored);
        assertNotSame(original, restored);
        AccountDetails account = new AccountDetails("Ada", "reader", "binding", "party", "enterprise", "Name",
                "issuer", "subject", 1, 2, "later", List.of("reader"), List.of(new SystemData("sys")),
                new ProfileData("profile", Map.of("tier", "one")), new SessionData("session", Map.of("ip", "local")), new Profile[]{new Profile("Bob", 4)}, "hidden");
        AccountDetails roundTrip = account.fromJson(account.toJson(true));
        assertEquals(account.accountName(), roundTrip.accountName());
        assertEquals(account.systems(), roundTrip.systems());
        assertEquals(account.profile(), roundTrip.profile());
        assertEquals(account.session(), roundTrip.session());
        assertArrayEquals(account.sessions(), roundTrip.sessions());
        assertNull(roundTrip.secret());
    }

    @Test
    void writesGeneratedTypeScriptForCompilation() throws Exception
    {
        Path root = Path.of("target", "record-typescript-generated");
        Files.createDirectories(root);
        var prior = IComponent.getCurrentAppFile().get();
        try
        {
            IComponent.getCurrentAppFile().set(root.toFile());
            writeGenerated(root, new Profile("", 0));
            writeGenerated(root, new SystemData(""));
            writeGenerated(root, new ProfileData("", Map.of()));
            writeGenerated(root, new SessionData("", Map.of()));
            writeGenerated(root, new OptionalData(Optional.empty()));
            writeGenerated(root, new GenericData<>(""));
            writeGenerated(root, new GenericHolder(List.of()));
            writeGenerated(root, new RecursiveData("", null));
            writeGenerated(root, new AccountDetails("", "", "", "", "", "", "", "", 0, 0, "", List.of(), List.of(), null, null, new Profile[0], ""));
            writeGenerated(root, new ClassProfile("", 0, null, List.of()));
        }
        finally
        {
            IComponent.getCurrentAppFile().set(prior);
        }
    }

    private static void writeGenerated(Path root, INgDataType<?> value) throws Exception
    {
        String name = AnnotationUtils.getTsFilename(value.getClass());
        Path file = root.resolve(Path.of("src", "app", IComponent.getClassDirectory(value.getClass()), name, name + ".ts"));
        Files.createDirectories(file.getParent());
        Files.writeString(file, value.renderClassTs());
    }

    @Test
    void importsNestedRecordTypes() throws Exception
    {
        var prior = IComponent.getCurrentAppFile().get();
        try
        {
            IComponent.getCurrentAppFile().set(Path.of("target", "record-import-test").toFile());
            var account = new AccountDetails("", "", "", "", "", "", "", "", 0, 0, "", List.of(), List.of(), null, null, new Profile[0], "");
            String imports = account.renderImports().toString();
            assertTrue(imports.contains("import { Profile }"), imports);
            assertTrue(imports.contains("import { SystemData }"), imports);
            assertTrue(imports.contains("import { SessionData }"), imports);
        }
        finally
        {
            IComponent.getCurrentAppFile().set(prior);
        }
    }

}
