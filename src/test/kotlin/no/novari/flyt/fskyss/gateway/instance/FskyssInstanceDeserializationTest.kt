package no.novari.flyt.fskyss.gateway.instance

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class FskyssInstanceDeserializationTest {
    private val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()

    @Test
    fun `deserializes when required fields are present as empty strings`() {
        val instance = objectMapper.readValue(validPayload(), FskyssInstance::class.java)

        assertEquals("", instance.order.caseReference)
        assertEquals("", instance.orderParts.first().originAlias)
        assertEquals("", instance.student.middleName)
        assertEquals("", instance.student.email)
        assertEquals("", instance.student.phone)
        assertEquals("", instance.guardians.first().middleName)
        assertEquals("", instance.guardians.first().email)
        assertEquals("", instance.guardians.first().phone)
    }

    @Test
    fun `fails deserialization when required fields are missing`() {
        val requiredFieldPaths =
            listOf(
                "order.case_reference",
                "order_parts[0].origin_alias",
                "student.middle_name",
                "student.email",
                "student.phone",
                "guardians[0].middle_name",
                "guardians[0].email",
                "guardians[0].phone",
            )

        requiredFieldPaths.forEach { fieldPath ->
            assertThrows(MismatchedInputException::class.java) {
                objectMapper.readValue(payloadWithout(fieldPath), FskyssInstance::class.java)
            }
        }
    }

    private fun payloadWithout(fieldPath: String): String {
        val root = objectMapper.readTree(validPayload()) as ObjectNode
        when (fieldPath) {
            "order.case_reference" -> (root.get("order") as ObjectNode).remove("case_reference")
            "order_parts[0].origin_alias" -> {
                val firstOrderPart = ((root.get("order_parts") as ArrayNode).get(0) as ObjectNode)
                firstOrderPart.remove("origin_alias")
            }
            "student.middle_name" -> (root.get("student") as ObjectNode).remove("middle_name")
            "student.email" -> (root.get("student") as ObjectNode).remove("email")
            "student.phone" -> (root.get("student") as ObjectNode).remove("phone")
            "guardians[0].middle_name" -> {
                val firstGuardian = ((root.get("guardians") as ArrayNode).get(0) as ObjectNode)
                firstGuardian.remove("middle_name")
            }
            "guardians[0].email" -> {
                val firstGuardian = ((root.get("guardians") as ArrayNode).get(0) as ObjectNode)
                firstGuardian.remove("email")
            }
            "guardians[0].phone" -> {
                val firstGuardian = ((root.get("guardians") as ArrayNode).get(0) as ObjectNode)
                firstGuardian.remove("phone")
            }
            else -> error("Unhandled field path: $fieldPath")
        }
        return objectMapper.writeValueAsString(root)
    }

    private fun validPayload(): String =
        """
        {
          "version": "1",
          "instance_id": "123456789",
          "document": {
            "file_name": "legeerklaering-ola-nordmann.pdf",
            "title": "Legeerklaering - 2024/2025",
            "mime_type": "application/pdf",
            "direction": "inbound",
            "category": "document",
            "content_base64": "Zm9v"
          },
          "order": {
            "order_id": "112617",
            "school_year": "2024",
            "from_date": "2024-08-19",
            "to_date": "2025-06-20",
            "status": "Approved",
            "decision_reason": "Avstand over 4 km",
            "case_reference": "",
            "is_county_decision": "true",
            "is_municipal_decision": "false",
            "is_shared_custody": "false",
            "is_urgent_temporary": "false",
            "requirements": [
              "Rullestol"
            ]
          },
          "order_parts": [
            {
              "origin": {
                "street_address": "Storgata 1",
                "postal_code": "5003",
                "city": "Bergen"
              },
              "origin_alias": "",
              "origin_type": "primary",
              "destination_name": "Bergen katedralskole - Hovedinngang",
              "from_date": "2024-08-19",
              "to_date": "2025-06-20",
              "approval_status": "approved",
              "decision_type": "county",
              "transport": {
                "uses_mass_transit": "false",
                "uses_taxi": "true",
                "uses_self": "false",
                "uses_boat": "false",
                "uses_ferry": "false",
                "uses_train": "false",
                "uses_taxi_shuttle": "false",
                "uses_self_shuttle": "false"
              }
            }
          ],
          "student": {
            "student_id": "87891",
            "ssn": "12345678901",
            "first_name": "Ola",
            "middle_name": "",
            "last_name": "Nordmann",
            "full_name": "Ola Nordmann",
            "birth_date": "190784",
            "address": {
              "street_address": "Storgata 1",
              "postal_code": "5003",
              "city": "Bergen"
            },
            "email": "",
            "phone": "",
            "municipality_number": "4601"
          },
          "school_class": {
            "class_name": "VG2B",
            "grade_level": "12"
          },
          "guardians": [
            {
              "role": "guardian1",
              "ssn": "12345678902",
              "first_name": "Kari",
              "middle_name": "",
              "last_name": "Nordmann",
              "full_name": "Kari Nordmann",
              "birth_date": "170384",
              "address": {
                "street_address": "Storgata 1",
                "postal_code": "5003",
                "city": "Bergen"
              },
              "email": "",
              "phone": ""
            }
          ],
          "school": {
            "name": "Bergen katedralskole",
            "school_number": "12345",
            "school_type": "VGS",
            "is_private": "false",
            "is_special": "false",
            "vigo_id": "1201019",
            "municipality": {
              "name": "Bergen",
              "municipality_number": "4601",
              "county_number": "46"
            },
            "organisation_name": "Vestland fylkeskommune"
          },
          "upload": {
            "uploaded_at": "2024-11-15T09:32:00Z",
            "uploaded_by": "saksbehandler@vestlandfk.no",
            "uploaded_by_identifier": "OLNO",
            "document_type": "Legeerklaering",
            "store_in_secure_zone": "true",
            "duplicate_handling": "reject_if_duplicate"
          }
        }
        """.trimIndent()
}
