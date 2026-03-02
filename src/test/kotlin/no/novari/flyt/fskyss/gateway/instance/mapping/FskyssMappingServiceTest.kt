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
    fun `maps fskyss instance to metadata compatible keys`() {
        val input =
            FskyssInstance(
                version = "1",
                instanceId = "123456789",
                document =
                    Document(
                        fileName = "legeerklaering-ola-nordmann.pdf",
                        title = "Legeerklaering - 2024/2025",
                        mimeType = "application/pdf",
                        direction = "inbound",
                        category = "document",
                        contentBase64 = "Zm9v",
                    ),
                order =
                    Order(
                        orderId = "112617",
                        schoolYear = "2024",
                        fromDate = "2024-08-19",
                        toDate = "2025-06-20",
                        status = "Approved",
                        decisionReason = "Avstand over 4 km",
                        caseReference = null,
                        isCountyDecision = "true",
                        isMunicipalDecision = "false",
                        isSharedCustody = "false",
                        isUrgentTemporary = "false",
                        requirements = listOf("Rullestol"),
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
                                    usesMassTransit = "false",
                                    usesTaxi = "true",
                                    usesSelf = "false",
                                    usesBoat = "false",
                                    usesFerry = "false",
                                    usesTrain = "false",
                                    usesTaxiShuttle = "false",
                                    usesSelfShuttle = "false",
                                ),
                        ),
                    ),
                student =
                    Student(
                        studentId = "87891",
                        ssn = "12345678901",
                        firstName = "Ola",
                        middleName = null,
                        lastName = "Nordmann",
                        fullName = "Ola Nordmann",
                        birthDate = "190784",
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
                        gradeLevel = "12",
                    ),
                guardians =
                    listOf(
                        Guardian(
                            role = "guardian1",
                            ssn = "12345678902",
                            firstName = "Kari",
                            middleName = null,
                            lastName = "Nordmann",
                            fullName = "Kari Nordmann",
                            birthDate = "170384",
                            address =
                                Address(
                                    streetAddress = "Storgata 1",
                                    postalCode = "5003",
                                    city = "Bergen",
                                ),
                            email = "kari.nordmann@example.com",
                            phone = "99887766",
                        ),
                        Guardian(
                            role = "guardian2",
                            ssn = "12345678903",
                            firstName = "Per",
                            middleName = null,
                            lastName = "Nordmann",
                            fullName = "Per Nordmann",
                            birthDate = "190784",
                            address =
                                Address(
                                    streetAddress = "Lillegata 5B",
                                    postalCode = "5004",
                                    city = "Bergen",
                                ),
                            email = "per.nordmann@example.com",
                            phone = "99887755",
                        ),
                    ),
                school =
                    School(
                        name = "Bergen katedralskole",
                        schoolNumber = "12345",
                        schoolType = "VGS",
                        isPrivate = "false",
                        isSpecial = "false",
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
                        uploadedByIdentifier = "OLNO",
                        documentType = "Legeerklaering",
                        storeInSecureZone = "true",
                        duplicateHandling = "reject_if_duplicate",
                    ),
            )

        val capturedFiles = mutableListOf<File>()
        val expectedFileId = UUID.fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6")

        val result =
            service.map(
                sourceApplicationId = 8L,
                incomingInstance = input,
                persistFile = { file ->
                    capturedFiles += file
                    expectedFileId
                },
            )

        assertEquals(1, capturedFiles.size)
        val capturedFile = capturedFiles.single()
        assertEquals("legeerklaering-ola-nordmann.pdf", capturedFile.name)
        assertEquals(8L, capturedFile.sourceApplicationId)
        assertEquals("123456789", capturedFile.sourceApplicationInstanceId)
        assertEquals(MediaType.APPLICATION_PDF, capturedFile.type)
        assertEquals("UTF-8", capturedFile.encoding)
        assertEquals("Zm9v", capturedFile.base64Contents)

        assertEquals("1", result.valuePerKey["version"])
        assertEquals("123456789", result.valuePerKey["instance_id"])
        assertEquals("87891", result.valuePerKey["student.student_id"])
        assertEquals("Ola", result.valuePerKey["student.first_name"])
        assertEquals("Ola Nordmann", result.valuePerKey["student.full_name"])
        assertEquals("190784", result.valuePerKey["student.birth_date"])
        assertEquals("", result.valuePerKey["student.middle_name"])
        assertEquals("", result.valuePerKey["student.email"])
        assertEquals("", result.valuePerKey["student.phone"])
        assertEquals("VG2B", result.valuePerKey["school_class.class_name"])
        assertEquals("112617", result.valuePerKey["order.order_id"])
        assertEquals("", result.valuePerKey["order.case_reference"])
        assertEquals("Rullestol", result.valuePerKey["order.requirements"])
        assertEquals(expectedFileId.toString(), result.valuePerKey["document.content_base64"])
        assertEquals("Legeerklaering", result.valuePerKey["upload.document_type"])
        assertEquals("OLNO", result.valuePerKey["upload.uploaded_by_identifier"])

        val orderPartObjects = result.objectCollectionPerKey.getValue("order_parts")
        assertEquals(1, orderPartObjects.size)
        val orderPart = orderPartObjects.single()
        assertEquals("Storgata 1", orderPart.valuePerKey["origin.street_address"])
        assertEquals("5003", orderPart.valuePerKey["origin.postal_code"])
        assertEquals("Bergen", orderPart.valuePerKey["origin.city"])
        assertEquals("", orderPart.valuePerKey["origin_alias"])
        assertEquals("primary", orderPart.valuePerKey["origin_type"])
        assertEquals("approved", orderPart.valuePerKey["approval_status"])
        assertEquals("true", orderPart.valuePerKey["transport.uses_taxi"])
        assertTrue(orderPart.objectCollectionPerKey.isEmpty())

        val guardianObjects = result.objectCollectionPerKey.getValue("guardians")
        assertEquals(2, guardianObjects.size)
        val firstGuardian = guardianObjects.first()
        assertEquals("guardian1", firstGuardian.valuePerKey["role"])
        assertEquals("Kari", firstGuardian.valuePerKey["first_name"])
        assertEquals("Kari Nordmann", firstGuardian.valuePerKey["full_name"])
        assertEquals("170384", firstGuardian.valuePerKey["birth_date"])
        assertEquals("", firstGuardian.valuePerKey["middle_name"])
        assertEquals("kari.nordmann@example.com", firstGuardian.valuePerKey["email"])
        assertEquals("99887766", firstGuardian.valuePerKey["phone"])
        assertEquals("Storgata 1", firstGuardian.valuePerKey["address.street_address"])
        assertTrue(firstGuardian.objectCollectionPerKey.isEmpty())

        val secondGuardian = guardianObjects.elementAt(1)
        assertEquals("guardian2", secondGuardian.valuePerKey["role"])
        assertEquals("Per", secondGuardian.valuePerKey["first_name"])
        assertEquals("Per Nordmann", secondGuardian.valuePerKey["full_name"])
        assertEquals("190784", secondGuardian.valuePerKey["birth_date"])
        assertEquals("", secondGuardian.valuePerKey["middle_name"])
        assertEquals("per.nordmann@example.com", secondGuardian.valuePerKey["email"])
        assertEquals("99887755", secondGuardian.valuePerKey["phone"])
        assertEquals("Lillegata 5B", secondGuardian.valuePerKey["address.street_address"])
        assertTrue(secondGuardian.objectCollectionPerKey.isEmpty())
    }
}
