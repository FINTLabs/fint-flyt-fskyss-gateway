package no.novari.flyt.fskyss.gateway.instance

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FskyssInstance(
    val version: Long,
    val instanceId: Long,
    val document: Document,
    val order: Order,
    val orderParts: List<OrderPart>,
    val student: Student,
    val schoolClass: SchoolClass,
    val guardians: List<Guardian>,
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
    val orderId: Long,
    val schoolYear: Int,
    val fromDate: String,
    val toDate: String,
    val status: String,
    val decisionReason: String,
    val caseReference: String?,
    val isCountyDecision: Boolean,
    val isMunicipalDecision: Boolean,
    val isSharedCustody: Boolean,
    val isUrgentTemporary: Boolean,
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
    val usesMassTransit: Boolean,
    val usesTaxi: Boolean,
    val usesSelf: Boolean,
    val usesBoat: Boolean,
    val usesFerry: Boolean,
    val usesTrain: Boolean,
    val usesTaxiShuttle: Boolean,
    val usesSelfShuttle: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Student(
    val studentId: Long,
    val ssn: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val address: Address,
    val email: String?,
    val phone: String?,
    val municipalityNumber: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SchoolClass(
    val className: String,
    val gradeLevel: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Guardian(
    val role: String,
    val ssn: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val address: Address,
    val email: String?,
    val phone: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class School(
    val name: String,
    val schoolNumber: Long,
    val schoolType: String,
    val isPrivate: Boolean,
    val isSpecial: Boolean,
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
    val documentType: String,
    val storeInSecureZone: Boolean,
    val duplicateHandling: String,
)
