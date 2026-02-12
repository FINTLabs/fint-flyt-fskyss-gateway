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
        val documentInstanceObjects =
            mapDocumentToInstanceObjects(
                persistFile = persistFile,
                sourceApplicationId = sourceApplicationId,
                sourceApplicationInstanceId = sourceApplicationInstanceId,
                document = incomingInstance.document,
            )

        val objectCollectionPerKey =
            mutableMapOf<String, Collection<InstanceObject>>(
                "documents" to documentInstanceObjects,
                "order" to listOf(mapOrderToInstanceObject(incomingInstance.order)),
                "orderParts" to mapOrderPartsToInstanceObjects(incomingInstance.orderParts),
                "student" to listOf(mapStudentToInstanceObject(incomingInstance.student)),
                "schoolClass" to listOf(mapSchoolClassToInstanceObject(incomingInstance.schoolClass)),
                "guardians" to mapGuardiansToInstanceObjects(incomingInstance.guardians),
                "school" to listOf(mapSchoolToInstanceObject(incomingInstance.school)),
                "upload" to listOf(mapUploadToInstanceObject(incomingInstance.upload)),
            )

        val valuePerKey =
            buildMap {
                putOrEmpty("version", incomingInstance.version)
                putOrEmpty("instanceId", incomingInstance.instanceId)
            }

        return InstanceObject(valuePerKey, objectCollectionPerKey)
    }

    private fun mapDocumentToInstanceObjects(
        persistFile: (File) -> UUID,
        sourceApplicationId: Long,
        sourceApplicationInstanceId: String,
        document: Document,
    ): List<InstanceObject> {
        val mediaType = MediaType.parseMediaType(document.mimeType)
        val file =
            File(
                name = document.fileName,
                type = mediaType,
                sourceApplicationId = sourceApplicationId,
                sourceApplicationInstanceId = sourceApplicationInstanceId,
                encoding = "UTF-8",
                base64Contents = document.contentBase64,
            )
        val fileId = persistFile(file)

        return listOf(
            InstanceObject(
                valuePerKey =
                    buildMap {
                        putOrEmpty("title", document.title)
                        putOrEmpty("fileName", document.fileName)
                        putOrEmpty("mediaType", mediaType.toString())
                        putOrEmpty("file", fileId)
                        putOrEmpty("direction", document.direction)
                        putOrEmpty("category", document.category)
                        putOrEmpty("mainDocument", true)
                    },
            ),
        )
    }

    private fun mapOrderToInstanceObject(order: Order): InstanceObject {
        val valuePerKey =
            buildMap {
                putOrEmpty("orderId", order.orderId)
                putOrEmpty("schoolYear", order.schoolYear)
                putOrEmpty("fromDate", order.fromDate)
                putOrEmpty("toDate", order.toDate)
                putOrEmpty("status", order.status)
                putOrEmpty("decisionReason", order.decisionReason)
                putOrEmpty("caseReference", order.caseReference)
                putOrEmpty("isCountyDecision", order.isCountyDecision)
                putOrEmpty("isMunicipalDecision", order.isMunicipalDecision)
                putOrEmpty("isSharedCustody", order.isSharedCustody)
                putOrEmpty("isUrgentTemporary", order.isUrgentTemporary)
            }

        val objectCollectionPerKey =
            mutableMapOf<String, Collection<InstanceObject>>(
                "requirements" to mapRequirementsToInstanceObjects(order.requirements),
            )

        return InstanceObject(valuePerKey, objectCollectionPerKey)
    }

    private fun mapOrderPartsToInstanceObjects(orderParts: List<OrderPart>): List<InstanceObject> {
        return orderParts.map { orderPart ->
            val valuePerKey =
                buildMap {
                    putOrEmpty("originAlias", orderPart.originAlias)
                    putOrEmpty("originType", orderPart.originType)
                    putOrEmpty("destinationName", orderPart.destinationName)
                    putOrEmpty("fromDate", orderPart.fromDate)
                    putOrEmpty("toDate", orderPart.toDate)
                    putOrEmpty("approvalStatus", orderPart.approvalStatus)
                    putOrEmpty("decisionType", orderPart.decisionType)
                }

            val objectCollectionPerKey =
                mutableMapOf<String, Collection<InstanceObject>>(
                    "origin" to listOf(mapAddressToInstanceObject(orderPart.origin)),
                    "transport" to listOf(mapTransportToInstanceObject(orderPart.transport)),
                )

            InstanceObject(valuePerKey, objectCollectionPerKey)
        }
    }

    private fun mapStudentToInstanceObject(student: Student): InstanceObject {
        val valuePerKey =
            buildMap {
                putOrEmpty("studentId", student.studentId)
                putCommonPersonFields(
                    ssn = student.ssn,
                    firstName = student.firstName,
                    middleName = student.middleName,
                    lastName = student.lastName,
                    email = student.email,
                    phone = student.phone,
                )
                putOrEmpty("municipalityNumber", student.municipalityNumber)
            }

        val objectCollectionPerKey =
            mutableMapOf<String, Collection<InstanceObject>>(
                "address" to listOf(mapAddressToInstanceObject(student.address)),
            )

        return InstanceObject(valuePerKey, objectCollectionPerKey)
    }

    private fun mapSchoolClassToInstanceObject(schoolClass: SchoolClass): InstanceObject {
        return InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty("className", schoolClass.className)
                    putOrEmpty("gradeLevel", schoolClass.gradeLevel)
                },
        )
    }

    private fun mapGuardiansToInstanceObjects(guardians: List<Guardian>): List<InstanceObject> {
        return guardians.map { guardian ->
            val valuePerKey =
                buildMap {
                    putOrEmpty("role", guardian.role)
                    putCommonPersonFields(
                        ssn = guardian.ssn,
                        firstName = guardian.firstName,
                        middleName = guardian.middleName,
                        lastName = guardian.lastName,
                        email = guardian.email,
                        phone = guardian.phone,
                    )
                }
            val objectCollectionPerKey =
                mutableMapOf<String, Collection<InstanceObject>>(
                    "address" to listOf(mapAddressToInstanceObject(guardian.address)),
                )

            InstanceObject(valuePerKey, objectCollectionPerKey)
        }
    }

    private fun mapSchoolToInstanceObject(school: School): InstanceObject {
        val valuePerKey =
            buildMap {
                putOrEmpty("name", school.name)
                putOrEmpty("schoolNumber", school.schoolNumber)
                putOrEmpty("schoolType", school.schoolType)
                putOrEmpty("isPrivate", school.isPrivate)
                putOrEmpty("isSpecial", school.isSpecial)
                putOrEmpty("vigoId", school.vigoId)
                putOrEmpty("organisationName", school.organisationName)
            }

        val objectCollectionPerKey =
            mutableMapOf<String, Collection<InstanceObject>>(
                "municipality" to listOf(mapMunicipalityToInstanceObject(school.municipality)),
            )

        return InstanceObject(valuePerKey, objectCollectionPerKey)
    }

    private fun mapUploadToInstanceObject(upload: Upload): InstanceObject {
        return InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty("uploadedAt", upload.uploadedAt)
                    putOrEmpty("uploadedBy", upload.uploadedBy)
                    putOrEmpty("documentType", upload.documentType)
                    putOrEmpty("storeInSecureZone", upload.storeInSecureZone)
                    putOrEmpty("duplicateHandling", upload.duplicateHandling)
                },
        )
    }

    private fun mapMunicipalityToInstanceObject(municipality: Municipality): InstanceObject {
        return InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty("name", municipality.name)
                    putOrEmpty("municipalityNumber", municipality.municipalityNumber)
                    putOrEmpty("countyNumber", municipality.countyNumber)
                },
        )
    }

    private fun mapAddressToInstanceObject(address: Address): InstanceObject {
        return InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty("streetAddress", address.streetAddress)
                    putOrEmpty("postalCode", address.postalCode)
                    putOrEmpty("city", address.city)
                },
        )
    }

    private fun mapTransportToInstanceObject(transport: Transport): InstanceObject {
        return InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty("usesMassTransit", transport.usesMassTransit)
                    putOrEmpty("usesTaxi", transport.usesTaxi)
                    putOrEmpty("usesSelf", transport.usesSelf)
                    putOrEmpty("usesBoat", transport.usesBoat)
                    putOrEmpty("usesFerry", transport.usesFerry)
                    putOrEmpty("usesTrain", transport.usesTrain)
                    putOrEmpty("usesTaxiShuttle", transport.usesTaxiShuttle)
                    putOrEmpty("usesSelfShuttle", transport.usesSelfShuttle)
                },
        )
    }

    private fun mapRequirementsToInstanceObjects(values: List<String>): List<InstanceObject> {
        return values.map { value ->
            InstanceObject(
                valuePerKey =
                    buildMap {
                        putOrEmpty("requirement", value)
                    },
            )
        }
    }

    private fun MutableMap<String, String>.putCommonPersonFields(
        ssn: String,
        firstName: String,
        middleName: String?,
        lastName: String,
        email: String?,
        phone: String?,
    ) {
        putOrEmpty("ssn", ssn)
        putOrEmpty("firstName", firstName)
        putOrEmpty("middleName", middleName)
        putOrEmpty("lastName", lastName)
        putOrEmpty("email", email)
        putOrEmpty("phone", phone)
    }
}
