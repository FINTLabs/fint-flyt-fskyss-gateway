package no.novari.flyt.fskyss.gateway.instance

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FskyssInstance(
    val version: String,
    val instanceId: String,
    val document: Document,
    val order: Order,
    val orderParts: List<OrderPart>,
    val student: Student,
    val schoolClass: SchoolClass,
    val guardians: List<Guardian>,
    val correspondenceParties: List<CorrespondenceParty>,
    val school: School,
    val upload: Upload,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Document(
    val fileName: String,
    val title: String,
    val mimeType: String,
    val direction: String,
    val category: String,
    val contentBase64: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Order(
    val orderId: String,
    val schoolYear: String,
    val fromDate: String,
    val toDate: String,
    val status: String,
    val decisionReason: String,
    val caseReference: String?,
    val isCountyDecision: String,
    val isMunicipalDecision: String,
    val isSharedCustody: String,
    val isUrgentTemporary: String,
    val requirements: List<String>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OrderPart(
    val origin: Address,
    val originAlias: String?,
    val originType: String,
    val destinationName: String,
    val fromDate: String,
    val toDate: String,
    val approvalStatus: String,
    val decisionType: String,
    val transport: Transport,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Address(
    val streetAddress: String,
    val postalCode: String,
    val city: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Transport(
    val usesMassTransit: String,
    val usesTaxi: String,
    val usesSelf: String,
    val usesBoat: String,
    val usesFerry: String,
    val usesTrain: String,
    val usesTaxiShuttle: String,
    val usesSelfShuttle: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Student(
    val studentId: String,
    val ssn: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val fullName: String,
    val birthDate: String,
    val address: Address,
    val email: String?,
    val phone: String?,
    val municipalityNumber: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SchoolClass(
    val className: String,
    val gradeLevel: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Guardian(
    val role: String,
    val ssn: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val fullName: String,
    val birthDate: String,
    val address: Address,
    val email: String?,
    val phone: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CorrespondenceParty(
    val name: String,
    val orgNumber: String?,
    val ssn: String?,
    val type: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class School(
    val name: String,
    val schoolNumber: String,
    val schoolType: String,
    val isPrivate: String,
    val isSpecial: String,
    val vigoId: String,
    val municipality: Municipality,
    val organisationName: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Municipality(
    val name: String,
    val municipalityNumber: String,
    val countyNumber: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Upload(
    val uploadedAt: String,
    val uploadedBy: String,
    val uploadedByIdentifier: String,
    val documentType: String,
    val storeInSecureZone: String,
    val duplicateHandling: String,
)
