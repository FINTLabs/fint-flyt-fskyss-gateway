package no.novari.flyt.fskyss.gateway.instance.mapping

import no.novari.flyt.fskyss.gateway.instance.FskyssInstance
import no.novari.flyt.fskyss.gateway.instance.Guardian
import no.novari.flyt.fskyss.gateway.instance.OrderPart
import no.novari.flyt.gateway.webinstance.InstanceMapper
import no.novari.flyt.gateway.webinstance.model.File
import no.novari.flyt.gateway.webinstance.model.instance.InstanceObject
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FskyssMappingService : InstanceMapper<FskyssInstance> {
    override fun map(
        sourceApplicationId: Long,
        incomingInstance: FskyssInstance,
        persistFile: (File) -> UUID,
    ): InstanceObject {
        val sourceApplicationInstanceId = incomingInstance.instanceId.toString()
        val fileId =
            persistDocument(
                persistFile = persistFile,
                sourceApplicationId = sourceApplicationId,
                sourceApplicationInstanceId = sourceApplicationInstanceId,
                incomingInstance = incomingInstance,
            )

        val valuePerKey =
            buildMap {
                putOrEmpty("version", incomingInstance.version)
                putOrEmpty("instance_id", incomingInstance.instanceId)

                putOrEmpty("document.file_name", incomingInstance.document.fileName)
                putOrEmpty("document.title", incomingInstance.document.title)
                putOrEmpty("document.mime_type", incomingInstance.document.mimeType)
                putOrEmpty("document.direction", incomingInstance.document.direction)
                putOrEmpty("document.category", incomingInstance.document.category)
                putOrEmpty("document.content_base64", fileId)

                putOrEmpty("order.order_id", incomingInstance.order.orderId)
                putOrEmpty("order.school_year", incomingInstance.order.schoolYear)
                putOrEmpty("order.from_date", incomingInstance.order.fromDate)
                putOrEmpty("order.to_date", incomingInstance.order.toDate)
                putOrEmpty("order.status", incomingInstance.order.status)
                putOrEmpty("order.decision_reason", incomingInstance.order.decisionReason)
                putOrEmpty("order.case_reference", incomingInstance.order.caseReference)
                putOrEmpty("order.is_county_decision", incomingInstance.order.isCountyDecision)
                putOrEmpty("order.is_municipal_decision", incomingInstance.order.isMunicipalDecision)
                putOrEmpty("order.is_shared_custody", incomingInstance.order.isSharedCustody)
                putOrEmpty("order.is_urgent_temporary", incomingInstance.order.isUrgentTemporary)
                putOrEmpty("order.requirements", incomingInstance.order.requirements.joinToString(","))

                putOrEmpty("student.student_id", incomingInstance.student.studentId)
                putOrEmpty("student.ssn", incomingInstance.student.ssn)
                putOrEmpty("student.first_name", incomingInstance.student.firstName)
                putOrEmpty("student.middle_name", incomingInstance.student.middleName)
                putOrEmpty("student.last_name", incomingInstance.student.lastName)
                putOrEmpty("student.address.street_address", incomingInstance.student.address.streetAddress)
                putOrEmpty("student.address.postal_code", incomingInstance.student.address.postalCode)
                putOrEmpty("student.address.city", incomingInstance.student.address.city)
                putOrEmpty("student.email", incomingInstance.student.email)
                putOrEmpty("student.phone", incomingInstance.student.phone)
                putOrEmpty("student.municipality_number", incomingInstance.student.municipalityNumber)

                putOrEmpty("school_class.class_name", incomingInstance.schoolClass.className)
                putOrEmpty("school_class.grade_level", incomingInstance.schoolClass.gradeLevel)

                putOrEmpty("school.name", incomingInstance.school.name)
                putOrEmpty("school.school_number", incomingInstance.school.schoolNumber)
                putOrEmpty("school.school_type", incomingInstance.school.schoolType)
                putOrEmpty("school.is_private", incomingInstance.school.isPrivate)
                putOrEmpty("school.is_special", incomingInstance.school.isSpecial)
                putOrEmpty("school.vigo_id", incomingInstance.school.vigoId)
                putOrEmpty("school.municipality.name", incomingInstance.school.municipality.name)
                putOrEmpty(
                    "school.municipality.municipality_number",
                    incomingInstance.school.municipality.municipalityNumber,
                )
                putOrEmpty("school.municipality.county_number", incomingInstance.school.municipality.countyNumber)
                putOrEmpty("school.organisation_name", incomingInstance.school.organisationName)

                putOrEmpty("upload.uploaded_at", incomingInstance.upload.uploadedAt)
                putOrEmpty("upload.uploaded_by", incomingInstance.upload.uploadedBy)
                putOrEmpty("upload.document_type", incomingInstance.upload.documentType)
                putOrEmpty("upload.store_in_secure_zone", incomingInstance.upload.storeInSecureZone)
                putOrEmpty("upload.duplicate_handling", incomingInstance.upload.duplicateHandling)
            }

        val objectCollectionPerKey =
            mutableMapOf<String, Collection<InstanceObject>>(
                "order_parts" to mapOrderPartsToInstanceObjects(incomingInstance.orderParts),
                "guardians" to mapGuardiansToInstanceObjects(incomingInstance.guardians),
            )

        return InstanceObject(valuePerKey, objectCollectionPerKey)
    }

    private fun persistDocument(
        persistFile: (File) -> UUID,
        sourceApplicationId: Long,
        sourceApplicationInstanceId: String,
        incomingInstance: FskyssInstance,
    ): UUID {
        val mediaType = MediaType.parseMediaType(incomingInstance.document.mimeType)
        val file =
            File(
                name = incomingInstance.document.fileName,
                type = mediaType,
                sourceApplicationId = sourceApplicationId,
                sourceApplicationInstanceId = sourceApplicationInstanceId,
                encoding = "UTF-8",
                base64Contents = incomingInstance.document.contentBase64,
            )
        return persistFile(file)
    }

    private fun mapOrderPartsToInstanceObjects(orderParts: List<OrderPart>): List<InstanceObject> {
        return orderParts.map { orderPart ->
            InstanceObject(
                valuePerKey =
                    buildMap {
                        putOrEmpty("origin.street_address", orderPart.origin.streetAddress)
                        putOrEmpty("origin.postal_code", orderPart.origin.postalCode)
                        putOrEmpty("origin.city", orderPart.origin.city)
                        putOrEmpty("origin_alias", orderPart.originAlias)
                        putOrEmpty("origin_type", orderPart.originType)
                        putOrEmpty("destination_name", orderPart.destinationName)
                        putOrEmpty("from_date", orderPart.fromDate)
                        putOrEmpty("to_date", orderPart.toDate)
                        putOrEmpty("approval_status", orderPart.approvalStatus)
                        putOrEmpty("decision_type", orderPart.decisionType)
                        putOrEmpty("transport.uses_mass_transit", orderPart.transport.usesMassTransit)
                        putOrEmpty("transport.uses_taxi", orderPart.transport.usesTaxi)
                        putOrEmpty("transport.uses_self", orderPart.transport.usesSelf)
                        putOrEmpty("transport.uses_boat", orderPart.transport.usesBoat)
                        putOrEmpty("transport.uses_ferry", orderPart.transport.usesFerry)
                        putOrEmpty("transport.uses_train", orderPart.transport.usesTrain)
                        putOrEmpty("transport.uses_taxi_shuttle", orderPart.transport.usesTaxiShuttle)
                        putOrEmpty("transport.uses_self_shuttle", orderPart.transport.usesSelfShuttle)
                    },
            )
        }
    }

    private fun mapGuardiansToInstanceObjects(guardians: List<Guardian>): List<InstanceObject> {
        return guardians.map { guardian ->
            InstanceObject(
                valuePerKey =
                    buildMap {
                        putOrEmpty("role", guardian.role)
                        putOrEmpty("ssn", guardian.ssn)
                        putOrEmpty("first_name", guardian.firstName)
                        putOrEmpty("middle_name", guardian.middleName)
                        putOrEmpty("last_name", guardian.lastName)
                        putOrEmpty("address.street_address", guardian.address.streetAddress)
                        putOrEmpty("address.postal_code", guardian.address.postalCode)
                        putOrEmpty("address.city", guardian.address.city)
                        putOrEmpty("email", guardian.email)
                        putOrEmpty("phone", guardian.phone)
                    },
            )
        }
    }
}
