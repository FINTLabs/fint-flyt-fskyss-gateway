package no.novari.flyt.fskyss.gateway.instance

import no.novari.flyt.fskyss.gateway.instance.mapping.FskyssMappingService
import no.novari.flyt.gateway.webinstance.InstanceProcessor
import no.novari.flyt.gateway.webinstance.InstanceProcessorFactoryService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FskyssProcessorConfiguration {
    @Bean
    fun fskyssProcessor(
        instanceProcessorFactoryService: InstanceProcessorFactoryService,
        fskyssMappingService: FskyssMappingService,
    ): InstanceProcessor<FskyssInstance> {
        return instanceProcessorFactoryService.createInstanceProcessor(
            "fskyss",
            { instance -> instance.instanceId.toString() },
            fskyssMappingService,
        )
    }
}
