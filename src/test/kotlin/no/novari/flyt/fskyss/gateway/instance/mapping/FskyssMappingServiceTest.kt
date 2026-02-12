package no.novari.flyt.fskyss.gateway.instance.mapping

import no.novari.flyt.fskyss.gateway.instance.Address
import no.novari.flyt.fskyss.gateway.instance.Document
import no.novari.flyt.fskyss.gateway.instance.FskyssInstance
import no.novari.flyt.fskyss.gateway.instance.Guardian
import no.novari.flyt.fskyss.gateway.instance.Municipality
import no.novari.flyt.fskyss.gateway.instance.Order
import no.novari.flyt.fskyss.gateway.instance.OrderPart
import no.novari.flyt.fskyss.gateway.instance.School
import no.novari.flyt.fskyss.gateway.instance.SchoolClass
import no.novari.flyt.fskyss.gateway.instance.Student
import no.novari.flyt.fskyss.gateway.instance.Transport
import no.novari.flyt.fskyss.gateway.instance.Upload
import no.novari.flyt.gateway.webinstance.model.File
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.util.UUID

class FskyssMappingServiceTest {
    private val service = FskyssMappingService()

    @Test
    fun `maps fskyss instance with collections and main document`() {
        val input =
            FskyssInstance(
                version = 1L,
                instanceId = 19643037L,
                document =
                    Document(
                        fileName = "fskyss-documentation.pdf",
                        title = "Fskyss dokumentasjon",
                        mimeType = "application/pdf",
                        direction = "inbound",
                        category = "document",
                        contentBase64 = "Zm9v",
                    ),
                order =
                    Order(
                        orderId = 112617L,
                        schoolYear = 2024,
                        fromDate = "2024-08-19",
                        toDate = "2025-06-20",
                        status = "Approved",
                        decisionReason = "Distance over 4 km",
                        caseReference = null,
                        isCountyDecision = true,
                        isMunicipalDecision = false,
                        isSharedCustody = false,
                        isUrgentTemporary = false,
                        requirements = listOf("Wheelchair"),
                    ),
                orderParts =
                    listOf(
                        OrderPart(
                            origin =
                                Address(
                                    streetAddress = "Storgata 1",
                                    postalCode = "5003",
                                    city = "Bergen",
                                ),
                            originAlias = null,
                            originType = "primary",
                            destinationName = "Bergen katedralskole - Hovedinngang",
                            fromDate = "2024-08-19",
                            toDate = "2025-06-20",
                            approvalStatus = "approved",
                            decisionType = "county",
                            transport =
                                Transport(
                                    usesMassTransit = false,
                                    usesTaxi = true,
                                    usesSelf = false,
                                    usesBoat = false,
                                    usesFerry = false,
                                    usesTrain = false,
                                    usesTaxiShuttle = false,
                                    usesSelfShuttle = false,
                                ),
                        ),
                    ),
                student =
                    Student(
                        studentId = 87891L,
                        ssn = "12345678901",
                        firstName = "Ola",
                        middleName = null,
                        lastName = "Nordmann",
                        address =
                            Address(
                                streetAddress = "Storgata 1",
                                postalCode = "5003",
                                city = "Bergen",
                            ),
                        email = null,
                        phone = null,
                        municipalityNumber = "4601",
                    ),
                schoolClass =
                    SchoolClass(
                        className = "VG2B",
                        gradeLevel = 12,
                    ),
                guardians =
                    listOf(
                        Guardian(
                            role = "guardian1",
                            ssn = "12345678902",
                            firstName = "Kari",
                            middleName = null,
                            lastName = "Nordmann",
                            address =
                                Address(
                                    streetAddress = "Storgata 1",
                                    postalCode = "5003",
                                    city = "Bergen",
                                ),
                            email = "kari.nordmann@example.com",
                            phone = "99887766",
                        ),
                    ),
                school =
                    School(
                        name = "Bergen katedralskole",
                        schoolNumber = 12345L,
                        schoolType = "VGS",
                        isPrivate = false,
                        isSpecial = false,
                        vigoId = "1201019",
                        municipality =
                            Municipality(
                                name = "Bergen",
                                municipalityNumber = "4601",
                                countyNumber = "46",
                            ),
                        organisationName = "Vestland fylkeskommune",
                    ),
                upload =
                    Upload(
                        uploadedAt = "2024-11-15T09:32:00Z",
                        uploadedBy = "saksbehandler@vestlandfk.no",
                        documentType = "Legeerklaering",
                        storeInSecureZone = true,
                        duplicateHandling = "reject_if_duplicate",
                    ),
            )

        val capturedFiles = mutableListOf<File>()
        val expectedFileId = UUID.fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6")

        val result =
            service.map(
                sourceApplicationId = 99L,
                incomingInstance = input,
                persistFile = { file ->
                    capturedFiles += file
                    expectedFileId
                },
            )

        assertEquals(1, capturedFiles.size)
        val capturedFile = capturedFiles.single()
        assertEquals("fskyss-documentation.pdf", capturedFile.name)
        assertEquals(99L, capturedFile.sourceApplicationId)
        assertEquals("19643037", capturedFile.sourceApplicationInstanceId)
        assertEquals(MediaType.APPLICATION_PDF, capturedFile.type)
        assertEquals("UTF-8", capturedFile.encoding)
        assertEquals("Zm9v", capturedFile.base64Contents)

        assertEquals(
            mapOf(
                "version" to "1",
                "instanceId" to "19643037",
            ),
            result.valuePerKey,
        )

        val documentObjects = result.objectCollectionPerKey.getValue("documents")
        assertEquals(1, documentObjects.size)
        val documentObject = documentObjects.single()
        assertEquals(
            mapOf(
                "title" to "Fskyss dokumentasjon",
                "fileName" to "fskyss-documentation.pdf",
                "mediaType" to "application/pdf",
                "file" to expectedFileId.toString(),
                "direction" to "inbound",
                "category" to "document",
                "mainDocument" to "true",
            ),
            documentObject.valuePerKey,
        )
        assertTrue(documentObject.objectCollectionPerKey.isEmpty())

        val orderObjects = result.objectCollectionPerKey.getValue("order")
        assertEquals(1, orderObjects.size)
        val orderObject = orderObjects.single()
        assertEquals(
            mapOf(
                "orderId" to "112617",
                "schoolYear" to "2024",
                "fromDate" to "2024-08-19",
                "toDate" to "2025-06-20",
                "status" to "Approved",
                "decisionReason" to "Distance over 4 km",
                "caseReference" to "",
                "isCountyDecision" to "true",
                "isMunicipalDecision" to "false",
                "isSharedCustody" to "false",
                "isUrgentTemporary" to "false",
            ),
            orderObject.valuePerKey,
        )
        assertEquals(
            listOf(mapOf("requirement" to "Wheelchair")),
            orderObject.objectCollectionPerKey.getValue("requirements").map { it.valuePerKey },
        )

        val orderPartObjects = result.objectCollectionPerKey.getValue("orderParts")
        assertEquals(1, orderPartObjects.size)
        val orderPartObject = orderPartObjects.single()
        assertEquals(
            mapOf(
                "originAlias" to "",
                "originType" to "primary",
                "destinationName" to "Bergen katedralskole - Hovedinngang",
                "fromDate" to "2024-08-19",
                "toDate" to "2025-06-20",
                "approvalStatus" to "approved",
                "decisionType" to "county",
            ),
            orderPartObject.valuePerKey,
        )
        assertEquals(
            listOf(
                mapOf(
                    "streetAddress" to "Storgata 1",
                    "postalCode" to "5003",
                    "city" to "Bergen",
                ),
            ),
            orderPartObject.objectCollectionPerKey.getValue("origin").map { it.valuePerKey },
        )
        assertEquals(
            listOf(
                mapOf(
                    "usesMassTransit" to "false",
                    "usesTaxi" to "true",
                    "usesSelf" to "false",
                    "usesBoat" to "false",
                    "usesFerry" to "false",
                    "usesTrain" to "false",
                    "usesTaxiShuttle" to "false",
                    "usesSelfShuttle" to "false",
                ),
            ),
            orderPartObject.objectCollectionPerKey.getValue("transport").map { it.valuePerKey },
        )

        val studentObjects = result.objectCollectionPerKey.getValue("student")
        assertEquals(1, studentObjects.size)
        val studentObject = studentObjects.single()
        assertEquals(
            mapOf(
                "studentId" to "87891",
                "ssn" to "12345678901",
                "firstName" to "Ola",
                "middleName" to "",
                "lastName" to "Nordmann",
                "email" to "",
                "phone" to "",
                "municipalityNumber" to "4601",
            ),
            studentObject.valuePerKey,
        )
        assertEquals(
            listOf(
                mapOf(
                    "streetAddress" to "Storgata 1",
                    "postalCode" to "5003",
                    "city" to "Bergen",
                ),
            ),
            studentObject.objectCollectionPerKey.getValue("address").map { it.valuePerKey },
        )

        val schoolClassObjects = result.objectCollectionPerKey.getValue("schoolClass")
        assertEquals(1, schoolClassObjects.size)
        assertEquals(
            mapOf(
                "className" to "VG2B",
                "gradeLevel" to "12",
            ),
            schoolClassObjects.single().valuePerKey,
        )

        val guardianObjects = result.objectCollectionPerKey.getValue("guardians")
        assertEquals(1, guardianObjects.size)
        val guardianObject = guardianObjects.single()
        assertEquals(
            mapOf(
                "role" to "guardian1",
                "ssn" to "12345678902",
                "firstName" to "Kari",
                "middleName" to "",
                "lastName" to "Nordmann",
                "email" to "kari.nordmann@example.com",
                "phone" to "99887766",
            ),
            guardianObject.valuePerKey,
        )
        assertEquals(
            listOf(
                mapOf(
                    "streetAddress" to "Storgata 1",
                    "postalCode" to "5003",
                    "city" to "Bergen",
                ),
            ),
            guardianObject.objectCollectionPerKey.getValue("address").map { it.valuePerKey },
        )

        val schoolObjects = result.objectCollectionPerKey.getValue("school")
        assertEquals(1, schoolObjects.size)
        val schoolObject = schoolObjects.single()
        assertEquals(
            mapOf(
                "name" to "Bergen katedralskole",
                "schoolNumber" to "12345",
                "schoolType" to "VGS",
                "isPrivate" to "false",
                "isSpecial" to "false",
                "vigoId" to "1201019",
                "organisationName" to "Vestland fylkeskommune",
            ),
            schoolObject.valuePerKey,
        )
        assertEquals(
            listOf(
                mapOf(
                    "name" to "Bergen",
                    "municipalityNumber" to "4601",
                    "countyNumber" to "46",
                ),
            ),
            schoolObject.objectCollectionPerKey.getValue("municipality").map { it.valuePerKey },
        )

        val uploadObjects = result.objectCollectionPerKey.getValue("upload")
        assertEquals(1, uploadObjects.size)
        assertEquals(
            mapOf(
                "uploadedAt" to "2024-11-15T09:32:00Z",
                "uploadedBy" to "saksbehandler@vestlandfk.no",
                "documentType" to "Legeerklaering",
                "storeInSecureZone" to "true",
                "duplicateHandling" to "reject_if_duplicate",
            ),
            uploadObjects.single().valuePerKey,
        )
        assertTrue(uploadObjects.single().objectCollectionPerKey.isEmpty())
    }
}
