package com.example.ui.art

import com.example.R

/**
 * ARCHIVO GENERADO — no editar a mano.
 *
 * Indice de laminas del juego. Existe porque el arte pasó a resolverse por
 * clave en tiempo de ejecucion — la misma especie tiene una lamina por rango
 * ("lobo_cenizo_elite"), y los subjefes de calabozo se nombran solos — y un
 * `when` escrito a mano con 1236 ramas se desincroniza a la primera.
 *
 * Se regenera con scratchpad/wire_art2.py a partir del contenido REAL de
 * res/drawable, de modo que no puede referenciar un recurso inexistente.
 */
object EldoriaArt {

    private val INDEX: Map<String, Int> = buildMap {
        val m = this
            block0(m)
            block1(m)
            block2(m)
            block3(m)
            block4(m)
            block5(m)
            block6(m)
    }

    private fun block0(m: MutableMap<String, Int>) {
        m["action_basic_clerigo"] = R.drawable.action_basic_clerigo
        m["action_basic_guerrero"] = R.drawable.action_basic_guerrero
        m["action_basic_mago"] = R.drawable.action_basic_mago
        m["action_basic_picaro"] = R.drawable.action_basic_picaro
        m["action_huir"] = R.drawable.action_huir
        m["action_zurron"] = R.drawable.action_zurron
        m["app_icon_fg_1784385781814"] = R.drawable.app_icon_fg_1784385781814
        m["bestiary_aethelgard_alquimista_fracturado_champion"] = R.drawable.bestiary_aethelgard_alquimista_fracturado_champion
        m["bestiary_aethelgard_alquimista_fracturado_elite"] = R.drawable.bestiary_aethelgard_alquimista_fracturado_elite
        m["bestiary_aethelgard_alquimista_fracturado_legendary"] = R.drawable.bestiary_aethelgard_alquimista_fracturado_legendary
        m["bestiary_aethelgard_alquimista_fracturado_normal"] = R.drawable.bestiary_aethelgard_alquimista_fracturado_normal
        m["bestiary_aethelgard_automata_marfileno_champion"] = R.drawable.bestiary_aethelgard_automata_marfileno_champion
        m["bestiary_aethelgard_automata_marfileno_elite"] = R.drawable.bestiary_aethelgard_automata_marfileno_elite
        m["bestiary_aethelgard_automata_marfileno_legendary"] = R.drawable.bestiary_aethelgard_automata_marfileno_legendary
        m["bestiary_aethelgard_automata_marfileno_normal"] = R.drawable.bestiary_aethelgard_automata_marfileno_normal
        m["bestiary_aethelgard_caballero_recordado_champion"] = R.drawable.bestiary_aethelgard_caballero_recordado_champion
        m["bestiary_aethelgard_caballero_recordado_elite"] = R.drawable.bestiary_aethelgard_caballero_recordado_elite
        m["bestiary_aethelgard_caballero_recordado_legendary"] = R.drawable.bestiary_aethelgard_caballero_recordado_legendary
        m["bestiary_aethelgard_caballero_recordado_normal"] = R.drawable.bestiary_aethelgard_caballero_recordado_normal
        m["bestiary_aethelgard_coro_disonante_champion"] = R.drawable.bestiary_aethelgard_coro_disonante_champion
        m["bestiary_aethelgard_coro_disonante_elite"] = R.drawable.bestiary_aethelgard_coro_disonante_elite
        m["bestiary_aethelgard_coro_disonante_legendary"] = R.drawable.bestiary_aethelgard_coro_disonante_legendary
        m["bestiary_aethelgard_coro_disonante_normal"] = R.drawable.bestiary_aethelgard_coro_disonante_normal
        m["bestiary_aethelgard_custodio_runas_champion"] = R.drawable.bestiary_aethelgard_custodio_runas_champion
        m["bestiary_aethelgard_custodio_runas_elite"] = R.drawable.bestiary_aethelgard_custodio_runas_elite
        m["bestiary_aethelgard_custodio_runas_legendary"] = R.drawable.bestiary_aethelgard_custodio_runas_legendary
        m["bestiary_aethelgard_custodio_runas_normal"] = R.drawable.bestiary_aethelgard_custodio_runas_normal
        m["bestiary_aethelgard_danzarina_eter_champion"] = R.drawable.bestiary_aethelgard_danzarina_eter_champion
        m["bestiary_aethelgard_danzarina_eter_elite"] = R.drawable.bestiary_aethelgard_danzarina_eter_elite
        m["bestiary_aethelgard_danzarina_eter_legendary"] = R.drawable.bestiary_aethelgard_danzarina_eter_legendary
        m["bestiary_aethelgard_danzarina_eter_normal"] = R.drawable.bestiary_aethelgard_danzarina_eter_normal
        m["bestiary_aethelgard_escarabajos_tumbales_champion"] = R.drawable.bestiary_aethelgard_escarabajos_tumbales_champion
        m["bestiary_aethelgard_escarabajos_tumbales_elite"] = R.drawable.bestiary_aethelgard_escarabajos_tumbales_elite
        m["bestiary_aethelgard_escarabajos_tumbales_legendary"] = R.drawable.bestiary_aethelgard_escarabajos_tumbales_legendary
        m["bestiary_aethelgard_escarabajos_tumbales_normal"] = R.drawable.bestiary_aethelgard_escarabajos_tumbales_normal
        m["bestiary_aethelgard_espectro_conclave_champion"] = R.drawable.bestiary_aethelgard_espectro_conclave_champion
        m["bestiary_aethelgard_espectro_conclave_elite"] = R.drawable.bestiary_aethelgard_espectro_conclave_elite
        m["bestiary_aethelgard_espectro_conclave_legendary"] = R.drawable.bestiary_aethelgard_espectro_conclave_legendary
        m["bestiary_aethelgard_espectro_conclave_normal"] = R.drawable.bestiary_aethelgard_espectro_conclave_normal
        m["bestiary_aethelgard_guardian_obelisco_champion"] = R.drawable.bestiary_aethelgard_guardian_obelisco_champion
        m["bestiary_aethelgard_guardian_obelisco_elite"] = R.drawable.bestiary_aethelgard_guardian_obelisco_elite
        m["bestiary_aethelgard_guardian_obelisco_legendary"] = R.drawable.bestiary_aethelgard_guardian_obelisco_legendary
        m["bestiary_aethelgard_guardian_obelisco_normal"] = R.drawable.bestiary_aethelgard_guardian_obelisco_normal
        m["bestiary_aethelgard_heraldo_culto_vacio_champion"] = R.drawable.bestiary_aethelgard_heraldo_culto_vacio_champion
        m["bestiary_aethelgard_heraldo_culto_vacio_elite"] = R.drawable.bestiary_aethelgard_heraldo_culto_vacio_elite
        m["bestiary_aethelgard_heraldo_culto_vacio_legendary"] = R.drawable.bestiary_aethelgard_heraldo_culto_vacio_legendary
        m["bestiary_aethelgard_heraldo_culto_vacio_normal"] = R.drawable.bestiary_aethelgard_heraldo_culto_vacio_normal
        m["bestiary_aethelgard_ojo_flotante_champion"] = R.drawable.bestiary_aethelgard_ojo_flotante_champion
        m["bestiary_aethelgard_ojo_flotante_elite"] = R.drawable.bestiary_aethelgard_ojo_flotante_elite
        m["bestiary_aethelgard_ojo_flotante_legendary"] = R.drawable.bestiary_aethelgard_ojo_flotante_legendary
        m["bestiary_aethelgard_ojo_flotante_normal"] = R.drawable.bestiary_aethelgard_ojo_flotante_normal
        m["bestiary_aethelgard_osario_ambulante_champion"] = R.drawable.bestiary_aethelgard_osario_ambulante_champion
        m["bestiary_aethelgard_osario_ambulante_elite"] = R.drawable.bestiary_aethelgard_osario_ambulante_elite
        m["bestiary_aethelgard_osario_ambulante_legendary"] = R.drawable.bestiary_aethelgard_osario_ambulante_legendary
        m["bestiary_aethelgard_osario_ambulante_normal"] = R.drawable.bestiary_aethelgard_osario_ambulante_normal
        m["bestiary_aethelgard_quimera_sellada_champion"] = R.drawable.bestiary_aethelgard_quimera_sellada_champion
        m["bestiary_aethelgard_quimera_sellada_elite"] = R.drawable.bestiary_aethelgard_quimera_sellada_elite
        m["bestiary_aethelgard_quimera_sellada_legendary"] = R.drawable.bestiary_aethelgard_quimera_sellada_legendary
        m["bestiary_aethelgard_quimera_sellada_normal"] = R.drawable.bestiary_aethelgard_quimera_sellada_normal
        m["bestiary_aethelgard_sanguijuela_mana_champion"] = R.drawable.bestiary_aethelgard_sanguijuela_mana_champion
        m["bestiary_aethelgard_sanguijuela_mana_elite"] = R.drawable.bestiary_aethelgard_sanguijuela_mana_elite
        m["bestiary_aethelgard_sanguijuela_mana_legendary"] = R.drawable.bestiary_aethelgard_sanguijuela_mana_legendary
        m["bestiary_aethelgard_sanguijuela_mana_normal"] = R.drawable.bestiary_aethelgard_sanguijuela_mana_normal
        m["bestiary_aethelgard_sepulturero_conclave_champion"] = R.drawable.bestiary_aethelgard_sepulturero_conclave_champion
        m["bestiary_aethelgard_sepulturero_conclave_elite"] = R.drawable.bestiary_aethelgard_sepulturero_conclave_elite
        m["bestiary_aethelgard_sepulturero_conclave_legendary"] = R.drawable.bestiary_aethelgard_sepulturero_conclave_legendary
        m["bestiary_aethelgard_sepulturero_conclave_normal"] = R.drawable.bestiary_aethelgard_sepulturero_conclave_normal
        m["bestiary_aethelgard_sombra_bibliotecario_champion"] = R.drawable.bestiary_aethelgard_sombra_bibliotecario_champion
        m["bestiary_aethelgard_sombra_bibliotecario_elite"] = R.drawable.bestiary_aethelgard_sombra_bibliotecario_elite
        m["bestiary_aethelgard_sombra_bibliotecario_legendary"] = R.drawable.bestiary_aethelgard_sombra_bibliotecario_legendary
        m["bestiary_aethelgard_sombra_bibliotecario_normal"] = R.drawable.bestiary_aethelgard_sombra_bibliotecario_normal
        m["bestiary_aethelgard_tejedor_vacio_champion"] = R.drawable.bestiary_aethelgard_tejedor_vacio_champion
        m["bestiary_aethelgard_tejedor_vacio_elite"] = R.drawable.bestiary_aethelgard_tejedor_vacio_elite
        m["bestiary_aethelgard_tejedor_vacio_legendary"] = R.drawable.bestiary_aethelgard_tejedor_vacio_legendary
        m["bestiary_aethelgard_tejedor_vacio_normal"] = R.drawable.bestiary_aethelgard_tejedor_vacio_normal
        m["bestiary_aethelgard_viuda_necropolis_champion"] = R.drawable.bestiary_aethelgard_viuda_necropolis_champion
        m["bestiary_aethelgard_viuda_necropolis_elite"] = R.drawable.bestiary_aethelgard_viuda_necropolis_elite
        m["bestiary_aethelgard_viuda_necropolis_legendary"] = R.drawable.bestiary_aethelgard_viuda_necropolis_legendary
        m["bestiary_aethelgard_viuda_necropolis_normal"] = R.drawable.bestiary_aethelgard_viuda_necropolis_normal
        m["bestiary_aetheria_ancla_gravitatoria_champion"] = R.drawable.bestiary_aetheria_ancla_gravitatoria_champion
        m["bestiary_aetheria_ancla_gravitatoria_elite"] = R.drawable.bestiary_aetheria_ancla_gravitatoria_elite
        m["bestiary_aetheria_ancla_gravitatoria_legendary"] = R.drawable.bestiary_aetheria_ancla_gravitatoria_legendary
        m["bestiary_aetheria_ancla_gravitatoria_normal"] = R.drawable.bestiary_aetheria_ancla_gravitatoria_normal
        m["bestiary_aetheria_arcangel_caido_champion"] = R.drawable.bestiary_aetheria_arcangel_caido_champion
        m["bestiary_aetheria_arcangel_caido_elite"] = R.drawable.bestiary_aetheria_arcangel_caido_elite
        m["bestiary_aetheria_arcangel_caido_legendary"] = R.drawable.bestiary_aetheria_arcangel_caido_legendary
        m["bestiary_aetheria_arcangel_caido_normal"] = R.drawable.bestiary_aetheria_arcangel_caido_normal
        m["bestiary_aetheria_cartografo_despues_champion"] = R.drawable.bestiary_aetheria_cartografo_despues_champion
        m["bestiary_aetheria_cartografo_despues_elite"] = R.drawable.bestiary_aetheria_cartografo_despues_elite
        m["bestiary_aetheria_cartografo_despues_legendary"] = R.drawable.bestiary_aetheria_cartografo_despues_legendary
        m["bestiary_aetheria_cartografo_despues_normal"] = R.drawable.bestiary_aetheria_cartografo_despues_normal
        m["bestiary_aetheria_cero_absoluto_champion"] = R.drawable.bestiary_aetheria_cero_absoluto_champion
        m["bestiary_aetheria_cero_absoluto_elite"] = R.drawable.bestiary_aetheria_cero_absoluto_elite
        m["bestiary_aetheria_cero_absoluto_legendary"] = R.drawable.bestiary_aetheria_cero_absoluto_legendary
        m["bestiary_aetheria_cero_absoluto_normal"] = R.drawable.bestiary_aetheria_cero_absoluto_normal
        m["bestiary_aetheria_coro_de_alas_champion"] = R.drawable.bestiary_aetheria_coro_de_alas_champion
        m["bestiary_aetheria_coro_de_alas_elite"] = R.drawable.bestiary_aetheria_coro_de_alas_elite
        m["bestiary_aetheria_coro_de_alas_legendary"] = R.drawable.bestiary_aetheria_coro_de_alas_legendary
        m["bestiary_aetheria_coro_de_alas_normal"] = R.drawable.bestiary_aetheria_coro_de_alas_normal
        m["bestiary_aetheria_devorador_entropia_champion"] = R.drawable.bestiary_aetheria_devorador_entropia_champion
        m["bestiary_aetheria_devorador_entropia_elite"] = R.drawable.bestiary_aetheria_devorador_entropia_elite
        m["bestiary_aetheria_devorador_entropia_legendary"] = R.drawable.bestiary_aetheria_devorador_entropia_legendary
        m["bestiary_aetheria_devorador_entropia_normal"] = R.drawable.bestiary_aetheria_devorador_entropia_normal
        m["bestiary_aetheria_dragon_muchos_lugares_champion"] = R.drawable.bestiary_aetheria_dragon_muchos_lugares_champion
        m["bestiary_aetheria_dragon_muchos_lugares_elite"] = R.drawable.bestiary_aetheria_dragon_muchos_lugares_elite
        m["bestiary_aetheria_dragon_muchos_lugares_legendary"] = R.drawable.bestiary_aetheria_dragon_muchos_lugares_legendary
        m["bestiary_aetheria_dragon_muchos_lugares_normal"] = R.drawable.bestiary_aetheria_dragon_muchos_lugares_normal
        m["bestiary_aetheria_eco_universo_muerto_champion"] = R.drawable.bestiary_aetheria_eco_universo_muerto_champion
        m["bestiary_aetheria_eco_universo_muerto_elite"] = R.drawable.bestiary_aetheria_eco_universo_muerto_elite
        m["bestiary_aetheria_eco_universo_muerto_legendary"] = R.drawable.bestiary_aetheria_eco_universo_muerto_legendary
        m["bestiary_aetheria_eco_universo_muerto_normal"] = R.drawable.bestiary_aetheria_eco_universo_muerto_normal
        m["bestiary_aetheria_fractal_viviente_champion"] = R.drawable.bestiary_aetheria_fractal_viviente_champion
        m["bestiary_aetheria_fractal_viviente_elite"] = R.drawable.bestiary_aetheria_fractal_viviente_elite
        m["bestiary_aetheria_fractal_viviente_legendary"] = R.drawable.bestiary_aetheria_fractal_viviente_legendary
        m["bestiary_aetheria_fractal_viviente_normal"] = R.drawable.bestiary_aetheria_fractal_viviente_normal
        m["bestiary_aetheria_heraldo_vacio_champion"] = R.drawable.bestiary_aetheria_heraldo_vacio_champion
        m["bestiary_aetheria_heraldo_vacio_elite"] = R.drawable.bestiary_aetheria_heraldo_vacio_elite
        m["bestiary_aetheria_heraldo_vacio_legendary"] = R.drawable.bestiary_aetheria_heraldo_vacio_legendary
        m["bestiary_aetheria_heraldo_vacio_normal"] = R.drawable.bestiary_aetheria_heraldo_vacio_normal
        m["bestiary_aetheria_juez_piedra_blanca_champion"] = R.drawable.bestiary_aetheria_juez_piedra_blanca_champion
        m["bestiary_aetheria_juez_piedra_blanca_elite"] = R.drawable.bestiary_aetheria_juez_piedra_blanca_elite
        m["bestiary_aetheria_juez_piedra_blanca_legendary"] = R.drawable.bestiary_aetheria_juez_piedra_blanca_legendary
        m["bestiary_aetheria_juez_piedra_blanca_normal"] = R.drawable.bestiary_aetheria_juez_piedra_blanca_normal
        m["bestiary_aetheria_larva_nebular_champion"] = R.drawable.bestiary_aetheria_larva_nebular_champion
        m["bestiary_aetheria_larva_nebular_elite"] = R.drawable.bestiary_aetheria_larva_nebular_elite
        m["bestiary_aetheria_larva_nebular_legendary"] = R.drawable.bestiary_aetheria_larva_nebular_legendary
        m["bestiary_aetheria_larva_nebular_normal"] = R.drawable.bestiary_aetheria_larva_nebular_normal
        m["bestiary_aetheria_pastor_estrellas_champion"] = R.drawable.bestiary_aetheria_pastor_estrellas_champion
        m["bestiary_aetheria_pastor_estrellas_elite"] = R.drawable.bestiary_aetheria_pastor_estrellas_elite
        m["bestiary_aetheria_pastor_estrellas_legendary"] = R.drawable.bestiary_aetheria_pastor_estrellas_legendary
        m["bestiary_aetheria_pastor_estrellas_normal"] = R.drawable.bestiary_aetheria_pastor_estrellas_normal
        m["bestiary_aetheria_peregrino_umbral_champion"] = R.drawable.bestiary_aetheria_peregrino_umbral_champion
        m["bestiary_aetheria_peregrino_umbral_elite"] = R.drawable.bestiary_aetheria_peregrino_umbral_elite
        m["bestiary_aetheria_peregrino_umbral_legendary"] = R.drawable.bestiary_aetheria_peregrino_umbral_legendary
        m["bestiary_aetheria_peregrino_umbral_normal"] = R.drawable.bestiary_aetheria_peregrino_umbral_normal
        m["bestiary_aetheria_simetria_rota_champion"] = R.drawable.bestiary_aetheria_simetria_rota_champion
        m["bestiary_aetheria_simetria_rota_elite"] = R.drawable.bestiary_aetheria_simetria_rota_elite
        m["bestiary_aetheria_simetria_rota_legendary"] = R.drawable.bestiary_aetheria_simetria_rota_legendary
        m["bestiary_aetheria_simetria_rota_normal"] = R.drawable.bestiary_aetheria_simetria_rota_normal
        m["bestiary_aetheria_tejedora_constelaciones_champion"] = R.drawable.bestiary_aetheria_tejedora_constelaciones_champion
        m["bestiary_aetheria_tejedora_constelaciones_elite"] = R.drawable.bestiary_aetheria_tejedora_constelaciones_elite
        m["bestiary_aetheria_tejedora_constelaciones_legendary"] = R.drawable.bestiary_aetheria_tejedora_constelaciones_legendary
        m["bestiary_aetheria_tejedora_constelaciones_normal"] = R.drawable.bestiary_aetheria_tejedora_constelaciones_normal
        m["bestiary_aetheria_testigo_sin_rostro_champion"] = R.drawable.bestiary_aetheria_testigo_sin_rostro_champion
        m["bestiary_aetheria_testigo_sin_rostro_elite"] = R.drawable.bestiary_aetheria_testigo_sin_rostro_elite
        m["bestiary_aetheria_testigo_sin_rostro_legendary"] = R.drawable.bestiary_aetheria_testigo_sin_rostro_legendary
        m["bestiary_aetheria_testigo_sin_rostro_normal"] = R.drawable.bestiary_aetheria_testigo_sin_rostro_normal
        m["bestiary_aetheria_titan_eter_champion"] = R.drawable.bestiary_aetheria_titan_eter_champion
        m["bestiary_aetheria_titan_eter_elite"] = R.drawable.bestiary_aetheria_titan_eter_elite
        m["bestiary_aetheria_titan_eter_legendary"] = R.drawable.bestiary_aetheria_titan_eter_legendary
        m["bestiary_aetheria_titan_eter_normal"] = R.drawable.bestiary_aetheria_titan_eter_normal
        m["bestiary_drakenhold_chaman_azufre_champion"] = R.drawable.bestiary_drakenhold_chaman_azufre_champion
        m["bestiary_drakenhold_chaman_azufre_elite"] = R.drawable.bestiary_drakenhold_chaman_azufre_elite
        m["bestiary_drakenhold_chaman_azufre_legendary"] = R.drawable.bestiary_drakenhold_chaman_azufre_legendary
        m["bestiary_drakenhold_chaman_azufre_normal"] = R.drawable.bestiary_drakenhold_chaman_azufre_normal
        m["bestiary_drakenhold_cultista_llama_champion"] = R.drawable.bestiary_drakenhold_cultista_llama_champion
        m["bestiary_drakenhold_cultista_llama_elite"] = R.drawable.bestiary_drakenhold_cultista_llama_elite
        m["bestiary_drakenhold_cultista_llama_legendary"] = R.drawable.bestiary_drakenhold_cultista_llama_legendary
        m["bestiary_drakenhold_cultista_llama_normal"] = R.drawable.bestiary_drakenhold_cultista_llama_normal
        m["bestiary_drakenhold_escupefuego_goblin_champion"] = R.drawable.bestiary_drakenhold_escupefuego_goblin_champion
        m["bestiary_drakenhold_escupefuego_goblin_elite"] = R.drawable.bestiary_drakenhold_escupefuego_goblin_elite
        m["bestiary_drakenhold_escupefuego_goblin_legendary"] = R.drawable.bestiary_drakenhold_escupefuego_goblin_legendary
        m["bestiary_drakenhold_escupefuego_goblin_normal"] = R.drawable.bestiary_drakenhold_escupefuego_goblin_normal
        m["bestiary_drakenhold_fenix_roto_champion"] = R.drawable.bestiary_drakenhold_fenix_roto_champion
        m["bestiary_drakenhold_fenix_roto_elite"] = R.drawable.bestiary_drakenhold_fenix_roto_elite
        m["bestiary_drakenhold_fenix_roto_legendary"] = R.drawable.bestiary_drakenhold_fenix_roto_legendary
        m["bestiary_drakenhold_fenix_roto_normal"] = R.drawable.bestiary_drakenhold_fenix_roto_normal
        m["bestiary_drakenhold_fundidor_almas_champion"] = R.drawable.bestiary_drakenhold_fundidor_almas_champion
        m["bestiary_drakenhold_fundidor_almas_elite"] = R.drawable.bestiary_drakenhold_fundidor_almas_elite
        m["bestiary_drakenhold_fundidor_almas_legendary"] = R.drawable.bestiary_drakenhold_fundidor_almas_legendary
        m["bestiary_drakenhold_fundidor_almas_normal"] = R.drawable.bestiary_drakenhold_fundidor_almas_normal
        m["bestiary_drakenhold_gigante_hollin_champion"] = R.drawable.bestiary_drakenhold_gigante_hollin_champion
        m["bestiary_drakenhold_gigante_hollin_elite"] = R.drawable.bestiary_drakenhold_gigante_hollin_elite
        m["bestiary_drakenhold_gigante_hollin_legendary"] = R.drawable.bestiary_drakenhold_gigante_hollin_legendary
        m["bestiary_drakenhold_gigante_hollin_normal"] = R.drawable.bestiary_drakenhold_gigante_hollin_normal
        m["bestiary_drakenhold_golem_magma_champion"] = R.drawable.bestiary_drakenhold_golem_magma_champion
        m["bestiary_drakenhold_golem_magma_elite"] = R.drawable.bestiary_drakenhold_golem_magma_elite
        m["bestiary_drakenhold_golem_magma_legendary"] = R.drawable.bestiary_drakenhold_golem_magma_legendary
        m["bestiary_drakenhold_golem_magma_normal"] = R.drawable.bestiary_drakenhold_golem_magma_normal
        m["bestiary_drakenhold_herrero_maldito_champion"] = R.drawable.bestiary_drakenhold_herrero_maldito_champion
        m["bestiary_drakenhold_herrero_maldito_elite"] = R.drawable.bestiary_drakenhold_herrero_maldito_elite
        m["bestiary_drakenhold_herrero_maldito_legendary"] = R.drawable.bestiary_drakenhold_herrero_maldito_legendary
        m["bestiary_drakenhold_herrero_maldito_normal"] = R.drawable.bestiary_drakenhold_herrero_maldito_normal
        m["bestiary_drakenhold_murcielago_igneo_champion"] = R.drawable.bestiary_drakenhold_murcielago_igneo_champion
        m["bestiary_drakenhold_murcielago_igneo_elite"] = R.drawable.bestiary_drakenhold_murcielago_igneo_elite
        m["bestiary_drakenhold_murcielago_igneo_legendary"] = R.drawable.bestiary_drakenhold_murcielago_igneo_legendary
        m["bestiary_drakenhold_murcielago_igneo_normal"] = R.drawable.bestiary_drakenhold_murcielago_igneo_normal
        m["bestiary_drakenhold_perro_obsidiana_champion"] = R.drawable.bestiary_drakenhold_perro_obsidiana_champion
        m["bestiary_drakenhold_perro_obsidiana_elite"] = R.drawable.bestiary_drakenhold_perro_obsidiana_elite
        m["bestiary_drakenhold_perro_obsidiana_legendary"] = R.drawable.bestiary_drakenhold_perro_obsidiana_legendary
        m["bestiary_drakenhold_perro_obsidiana_normal"] = R.drawable.bestiary_drakenhold_perro_obsidiana_normal
        m["bestiary_drakenhold_piromante_exiliado_champion"] = R.drawable.bestiary_drakenhold_piromante_exiliado_champion
        m["bestiary_drakenhold_piromante_exiliado_elite"] = R.drawable.bestiary_drakenhold_piromante_exiliado_elite
        m["bestiary_drakenhold_piromante_exiliado_legendary"] = R.drawable.bestiary_drakenhold_piromante_exiliado_legendary
        m["bestiary_drakenhold_piromante_exiliado_normal"] = R.drawable.bestiary_drakenhold_piromante_exiliado_normal
        m["bestiary_drakenhold_portador_brasas_champion"] = R.drawable.bestiary_drakenhold_portador_brasas_champion
        m["bestiary_drakenhold_portador_brasas_elite"] = R.drawable.bestiary_drakenhold_portador_brasas_elite
        m["bestiary_drakenhold_portador_brasas_legendary"] = R.drawable.bestiary_drakenhold_portador_brasas_legendary
        m["bestiary_drakenhold_portador_brasas_normal"] = R.drawable.bestiary_drakenhold_portador_brasas_normal
        m["bestiary_drakenhold_salamandra_escoria_champion"] = R.drawable.bestiary_drakenhold_salamandra_escoria_champion
    }

    private fun block1(m: MutableMap<String, Int>) {
        m["bestiary_drakenhold_salamandra_escoria_elite"] = R.drawable.bestiary_drakenhold_salamandra_escoria_elite
        m["bestiary_drakenhold_salamandra_escoria_legendary"] = R.drawable.bestiary_drakenhold_salamandra_escoria_legendary
        m["bestiary_drakenhold_salamandra_escoria_normal"] = R.drawable.bestiary_drakenhold_salamandra_escoria_normal
        m["bestiary_drakenhold_sierpe_fumarola_champion"] = R.drawable.bestiary_drakenhold_sierpe_fumarola_champion
        m["bestiary_drakenhold_sierpe_fumarola_elite"] = R.drawable.bestiary_drakenhold_sierpe_fumarola_elite
        m["bestiary_drakenhold_sierpe_fumarola_legendary"] = R.drawable.bestiary_drakenhold_sierpe_fumarola_legendary
        m["bestiary_drakenhold_sierpe_fumarola_normal"] = R.drawable.bestiary_drakenhold_sierpe_fumarola_normal
        m["bestiary_drakenhold_titan_fundicion_champion"] = R.drawable.bestiary_drakenhold_titan_fundicion_champion
        m["bestiary_drakenhold_titan_fundicion_elite"] = R.drawable.bestiary_drakenhold_titan_fundicion_elite
        m["bestiary_drakenhold_titan_fundicion_legendary"] = R.drawable.bestiary_drakenhold_titan_fundicion_legendary
        m["bestiary_drakenhold_titan_fundicion_normal"] = R.drawable.bestiary_drakenhold_titan_fundicion_normal
        m["bestiary_drakenhold_vulcanoide_menor_champion"] = R.drawable.bestiary_drakenhold_vulcanoide_menor_champion
        m["bestiary_drakenhold_vulcanoide_menor_elite"] = R.drawable.bestiary_drakenhold_vulcanoide_menor_elite
        m["bestiary_drakenhold_vulcanoide_menor_legendary"] = R.drawable.bestiary_drakenhold_vulcanoide_menor_legendary
        m["bestiary_drakenhold_vulcanoide_menor_normal"] = R.drawable.bestiary_drakenhold_vulcanoide_menor_normal
        m["bestiary_drakenhold_wyrm_obsidiana_champion"] = R.drawable.bestiary_drakenhold_wyrm_obsidiana_champion
        m["bestiary_drakenhold_wyrm_obsidiana_elite"] = R.drawable.bestiary_drakenhold_wyrm_obsidiana_elite
        m["bestiary_drakenhold_wyrm_obsidiana_legendary"] = R.drawable.bestiary_drakenhold_wyrm_obsidiana_legendary
        m["bestiary_drakenhold_wyrm_obsidiana_normal"] = R.drawable.bestiary_drakenhold_wyrm_obsidiana_normal
        m["bestiary_drakenhold_wyvern_ceniza_champion"] = R.drawable.bestiary_drakenhold_wyvern_ceniza_champion
        m["bestiary_drakenhold_wyvern_ceniza_elite"] = R.drawable.bestiary_drakenhold_wyvern_ceniza_elite
        m["bestiary_drakenhold_wyvern_ceniza_legendary"] = R.drawable.bestiary_drakenhold_wyvern_ceniza_legendary
        m["bestiary_drakenhold_wyvern_ceniza_normal"] = R.drawable.bestiary_drakenhold_wyvern_ceniza_normal
        m["bestiary_eldoria_alcaide_ruinas_champion"] = R.drawable.bestiary_eldoria_alcaide_ruinas_champion
        m["bestiary_eldoria_alcaide_ruinas_elite"] = R.drawable.bestiary_eldoria_alcaide_ruinas_elite
        m["bestiary_eldoria_alcaide_ruinas_legendary"] = R.drawable.bestiary_eldoria_alcaide_ruinas_legendary
        m["bestiary_eldoria_alcaide_ruinas_normal"] = R.drawable.bestiary_eldoria_alcaide_ruinas_normal
        m["bestiary_eldoria_avispa_reina_esmeralda_champion"] = R.drawable.bestiary_eldoria_avispa_reina_esmeralda_champion
        m["bestiary_eldoria_avispa_reina_esmeralda_elite"] = R.drawable.bestiary_eldoria_avispa_reina_esmeralda_elite
        m["bestiary_eldoria_avispa_reina_esmeralda_legendary"] = R.drawable.bestiary_eldoria_avispa_reina_esmeralda_legendary
        m["bestiary_eldoria_avispa_reina_esmeralda_normal"] = R.drawable.bestiary_eldoria_avispa_reina_esmeralda_normal
        m["bestiary_eldoria_bandido_camino_champion"] = R.drawable.bestiary_eldoria_bandido_camino_champion
        m["bestiary_eldoria_bandido_camino_elite"] = R.drawable.bestiary_eldoria_bandido_camino_elite
        m["bestiary_eldoria_bandido_camino_legendary"] = R.drawable.bestiary_eldoria_bandido_camino_legendary
        m["bestiary_eldoria_bandido_camino_normal"] = R.drawable.bestiary_eldoria_bandido_camino_normal
        m["bestiary_eldoria_ciervo_espectral_champion"] = R.drawable.bestiary_eldoria_ciervo_espectral_champion
        m["bestiary_eldoria_ciervo_espectral_elite"] = R.drawable.bestiary_eldoria_ciervo_espectral_elite
        m["bestiary_eldoria_ciervo_espectral_legendary"] = R.drawable.bestiary_eldoria_ciervo_espectral_legendary
        m["bestiary_eldoria_ciervo_espectral_normal"] = R.drawable.bestiary_eldoria_ciervo_espectral_normal
        m["bestiary_eldoria_cuervo_presagio_champion"] = R.drawable.bestiary_eldoria_cuervo_presagio_champion
        m["bestiary_eldoria_cuervo_presagio_elite"] = R.drawable.bestiary_eldoria_cuervo_presagio_elite
        m["bestiary_eldoria_cuervo_presagio_legendary"] = R.drawable.bestiary_eldoria_cuervo_presagio_legendary
        m["bestiary_eldoria_cuervo_presagio_normal"] = R.drawable.bestiary_eldoria_cuervo_presagio_normal
        m["bestiary_eldoria_druida_renegado_champion"] = R.drawable.bestiary_eldoria_druida_renegado_champion
        m["bestiary_eldoria_druida_renegado_elite"] = R.drawable.bestiary_eldoria_druida_renegado_elite
        m["bestiary_eldoria_druida_renegado_legendary"] = R.drawable.bestiary_eldoria_druida_renegado_legendary
        m["bestiary_eldoria_druida_renegado_normal"] = R.drawable.bestiary_eldoria_druida_renegado_normal
        m["bestiary_eldoria_duende_zarcero_champion"] = R.drawable.bestiary_eldoria_duende_zarcero_champion
        m["bestiary_eldoria_duende_zarcero_elite"] = R.drawable.bestiary_eldoria_duende_zarcero_elite
        m["bestiary_eldoria_duende_zarcero_legendary"] = R.drawable.bestiary_eldoria_duende_zarcero_legendary
        m["bestiary_eldoria_duende_zarcero_normal"] = R.drawable.bestiary_eldoria_duende_zarcero_normal
        m["bestiary_eldoria_ermitano_podrido_champion"] = R.drawable.bestiary_eldoria_ermitano_podrido_champion
        m["bestiary_eldoria_ermitano_podrido_elite"] = R.drawable.bestiary_eldoria_ermitano_podrido_elite
        m["bestiary_eldoria_ermitano_podrido_legendary"] = R.drawable.bestiary_eldoria_ermitano_podrido_legendary
        m["bestiary_eldoria_ermitano_podrido_normal"] = R.drawable.bestiary_eldoria_ermitano_podrido_normal
        m["bestiary_eldoria_espantapajaros_runico_champion"] = R.drawable.bestiary_eldoria_espantapajaros_runico_champion
        m["bestiary_eldoria_espantapajaros_runico_elite"] = R.drawable.bestiary_eldoria_espantapajaros_runico_elite
        m["bestiary_eldoria_espantapajaros_runico_legendary"] = R.drawable.bestiary_eldoria_espantapajaros_runico_legendary
        m["bestiary_eldoria_espantapajaros_runico_normal"] = R.drawable.bestiary_eldoria_espantapajaros_runico_normal
        m["bestiary_eldoria_jabali_coronado_champion"] = R.drawable.bestiary_eldoria_jabali_coronado_champion
        m["bestiary_eldoria_jabali_coronado_elite"] = R.drawable.bestiary_eldoria_jabali_coronado_elite
        m["bestiary_eldoria_jabali_coronado_legendary"] = R.drawable.bestiary_eldoria_jabali_coronado_legendary
        m["bestiary_eldoria_jabali_coronado_normal"] = R.drawable.bestiary_eldoria_jabali_coronado_normal
        m["bestiary_eldoria_lobo_cenizo_champion"] = R.drawable.bestiary_eldoria_lobo_cenizo_champion
        m["bestiary_eldoria_lobo_cenizo_elite"] = R.drawable.bestiary_eldoria_lobo_cenizo_elite
        m["bestiary_eldoria_lobo_cenizo_legendary"] = R.drawable.bestiary_eldoria_lobo_cenizo_legendary
        m["bestiary_eldoria_lobo_cenizo_normal"] = R.drawable.bestiary_eldoria_lobo_cenizo_normal
        m["bestiary_eldoria_luciernagas_palidas_champion"] = R.drawable.bestiary_eldoria_luciernagas_palidas_champion
        m["bestiary_eldoria_luciernagas_palidas_elite"] = R.drawable.bestiary_eldoria_luciernagas_palidas_elite
        m["bestiary_eldoria_luciernagas_palidas_legendary"] = R.drawable.bestiary_eldoria_luciernagas_palidas_legendary
        m["bestiary_eldoria_luciernagas_palidas_normal"] = R.drawable.bestiary_eldoria_luciernagas_palidas_normal
        m["bestiary_eldoria_musgoso_devorador_champion"] = R.drawable.bestiary_eldoria_musgoso_devorador_champion
        m["bestiary_eldoria_musgoso_devorador_elite"] = R.drawable.bestiary_eldoria_musgoso_devorador_elite
        m["bestiary_eldoria_musgoso_devorador_legendary"] = R.drawable.bestiary_eldoria_musgoso_devorador_legendary
        m["bestiary_eldoria_musgoso_devorador_normal"] = R.drawable.bestiary_eldoria_musgoso_devorador_normal
        m["bestiary_eldoria_niebla_del_vado_champion"] = R.drawable.bestiary_eldoria_niebla_del_vado_champion
        m["bestiary_eldoria_niebla_del_vado_elite"] = R.drawable.bestiary_eldoria_niebla_del_vado_elite
        m["bestiary_eldoria_niebla_del_vado_legendary"] = R.drawable.bestiary_eldoria_niebla_del_vado_legendary
        m["bestiary_eldoria_niebla_del_vado_normal"] = R.drawable.bestiary_eldoria_niebla_del_vado_normal
        m["bestiary_eldoria_oso_colmenero_champion"] = R.drawable.bestiary_eldoria_oso_colmenero_champion
        m["bestiary_eldoria_oso_colmenero_elite"] = R.drawable.bestiary_eldoria_oso_colmenero_elite
        m["bestiary_eldoria_oso_colmenero_legendary"] = R.drawable.bestiary_eldoria_oso_colmenero_legendary
        m["bestiary_eldoria_oso_colmenero_normal"] = R.drawable.bestiary_eldoria_oso_colmenero_normal
        m["bestiary_eldoria_raiz_estranguladora_champion"] = R.drawable.bestiary_eldoria_raiz_estranguladora_champion
        m["bestiary_eldoria_raiz_estranguladora_elite"] = R.drawable.bestiary_eldoria_raiz_estranguladora_elite
        m["bestiary_eldoria_raiz_estranguladora_legendary"] = R.drawable.bestiary_eldoria_raiz_estranguladora_legendary
        m["bestiary_eldoria_raiz_estranguladora_normal"] = R.drawable.bestiary_eldoria_raiz_estranguladora_normal
        m["bestiary_eldoria_sabueso_del_alba_champion"] = R.drawable.bestiary_eldoria_sabueso_del_alba_champion
        m["bestiary_eldoria_sabueso_del_alba_elite"] = R.drawable.bestiary_eldoria_sabueso_del_alba_elite
        m["bestiary_eldoria_sabueso_del_alba_legendary"] = R.drawable.bestiary_eldoria_sabueso_del_alba_legendary
        m["bestiary_eldoria_sabueso_del_alba_normal"] = R.drawable.bestiary_eldoria_sabueso_del_alba_normal
        m["bestiary_eldoria_zorro_dos_colas_champion"] = R.drawable.bestiary_eldoria_zorro_dos_colas_champion
        m["bestiary_eldoria_zorro_dos_colas_elite"] = R.drawable.bestiary_eldoria_zorro_dos_colas_elite
        m["bestiary_eldoria_zorro_dos_colas_legendary"] = R.drawable.bestiary_eldoria_zorro_dos_colas_legendary
        m["bestiary_eldoria_zorro_dos_colas_normal"] = R.drawable.bestiary_eldoria_zorro_dos_colas_normal
        m["bestiary_frostgard_alce_hielo_negro_champion"] = R.drawable.bestiary_frostgard_alce_hielo_negro_champion
        m["bestiary_frostgard_alce_hielo_negro_elite"] = R.drawable.bestiary_frostgard_alce_hielo_negro_elite
        m["bestiary_frostgard_alce_hielo_negro_legendary"] = R.drawable.bestiary_frostgard_alce_hielo_negro_legendary
        m["bestiary_frostgard_alce_hielo_negro_normal"] = R.drawable.bestiary_frostgard_alce_hielo_negro_normal
        m["bestiary_frostgard_alma_nevisca_champion"] = R.drawable.bestiary_frostgard_alma_nevisca_champion
        m["bestiary_frostgard_alma_nevisca_elite"] = R.drawable.bestiary_frostgard_alma_nevisca_elite
        m["bestiary_frostgard_alma_nevisca_legendary"] = R.drawable.bestiary_frostgard_alma_nevisca_legendary
        m["bestiary_frostgard_alma_nevisca_normal"] = R.drawable.bestiary_frostgard_alma_nevisca_normal
        m["bestiary_frostgard_arponero_ahogado_champion"] = R.drawable.bestiary_frostgard_arponero_ahogado_champion
        m["bestiary_frostgard_arponero_ahogado_elite"] = R.drawable.bestiary_frostgard_arponero_ahogado_elite
        m["bestiary_frostgard_arponero_ahogado_legendary"] = R.drawable.bestiary_frostgard_arponero_ahogado_legendary
        m["bestiary_frostgard_arponero_ahogado_normal"] = R.drawable.bestiary_frostgard_arponero_ahogado_normal
        m["bestiary_frostgard_aullador_escarcha_champion"] = R.drawable.bestiary_frostgard_aullador_escarcha_champion
        m["bestiary_frostgard_aullador_escarcha_elite"] = R.drawable.bestiary_frostgard_aullador_escarcha_elite
        m["bestiary_frostgard_aullador_escarcha_legendary"] = R.drawable.bestiary_frostgard_aullador_escarcha_legendary
        m["bestiary_frostgard_aullador_escarcha_normal"] = R.drawable.bestiary_frostgard_aullador_escarcha_normal
        m["bestiary_frostgard_bruja_solsticio_champion"] = R.drawable.bestiary_frostgard_bruja_solsticio_champion
        m["bestiary_frostgard_bruja_solsticio_elite"] = R.drawable.bestiary_frostgard_bruja_solsticio_elite
        m["bestiary_frostgard_bruja_solsticio_legendary"] = R.drawable.bestiary_frostgard_bruja_solsticio_legendary
        m["bestiary_frostgard_bruja_solsticio_normal"] = R.drawable.bestiary_frostgard_bruja_solsticio_normal
        m["bestiary_frostgard_corneja_hielo_champion"] = R.drawable.bestiary_frostgard_corneja_hielo_champion
        m["bestiary_frostgard_corneja_hielo_elite"] = R.drawable.bestiary_frostgard_corneja_hielo_elite
        m["bestiary_frostgard_corneja_hielo_legendary"] = R.drawable.bestiary_frostgard_corneja_hielo_legendary
        m["bestiary_frostgard_corneja_hielo_normal"] = R.drawable.bestiary_frostgard_corneja_hielo_normal
        m["bestiary_frostgard_cosa_bajo_glaciar_champion"] = R.drawable.bestiary_frostgard_cosa_bajo_glaciar_champion
        m["bestiary_frostgard_cosa_bajo_glaciar_elite"] = R.drawable.bestiary_frostgard_cosa_bajo_glaciar_elite
        m["bestiary_frostgard_cosa_bajo_glaciar_legendary"] = R.drawable.bestiary_frostgard_cosa_bajo_glaciar_legendary
        m["bestiary_frostgard_cosa_bajo_glaciar_normal"] = R.drawable.bestiary_frostgard_cosa_bajo_glaciar_normal
        m["bestiary_frostgard_doncella_ventisquero_champion"] = R.drawable.bestiary_frostgard_doncella_ventisquero_champion
        m["bestiary_frostgard_doncella_ventisquero_elite"] = R.drawable.bestiary_frostgard_doncella_ventisquero_elite
        m["bestiary_frostgard_doncella_ventisquero_legendary"] = R.drawable.bestiary_frostgard_doncella_ventisquero_legendary
        m["bestiary_frostgard_doncella_ventisquero_normal"] = R.drawable.bestiary_frostgard_doncella_ventisquero_normal
        m["bestiary_frostgard_espiritu_ventisca_champion"] = R.drawable.bestiary_frostgard_espiritu_ventisca_champion
        m["bestiary_frostgard_espiritu_ventisca_elite"] = R.drawable.bestiary_frostgard_espiritu_ventisca_elite
        m["bestiary_frostgard_espiritu_ventisca_legendary"] = R.drawable.bestiary_frostgard_espiritu_ventisca_legendary
        m["bestiary_frostgard_espiritu_ventisca_normal"] = R.drawable.bestiary_frostgard_espiritu_ventisca_normal
        m["bestiary_frostgard_estatua_sal_champion"] = R.drawable.bestiary_frostgard_estatua_sal_champion
        m["bestiary_frostgard_estatua_sal_elite"] = R.drawable.bestiary_frostgard_estatua_sal_elite
        m["bestiary_frostgard_estatua_sal_legendary"] = R.drawable.bestiary_frostgard_estatua_sal_legendary
        m["bestiary_frostgard_estatua_sal_normal"] = R.drawable.bestiary_frostgard_estatua_sal_normal
        m["bestiary_frostgard_lanzahielo_enano_champion"] = R.drawable.bestiary_frostgard_lanzahielo_enano_champion
        m["bestiary_frostgard_lanzahielo_enano_elite"] = R.drawable.bestiary_frostgard_lanzahielo_enano_elite
        m["bestiary_frostgard_lanzahielo_enano_legendary"] = R.drawable.bestiary_frostgard_lanzahielo_enano_legendary
        m["bestiary_frostgard_lanzahielo_enano_normal"] = R.drawable.bestiary_frostgard_lanzahielo_enano_normal
        m["bestiary_frostgard_mamut_sepulcral_champion"] = R.drawable.bestiary_frostgard_mamut_sepulcral_champion
        m["bestiary_frostgard_mamut_sepulcral_elite"] = R.drawable.bestiary_frostgard_mamut_sepulcral_elite
        m["bestiary_frostgard_mamut_sepulcral_legendary"] = R.drawable.bestiary_frostgard_mamut_sepulcral_legendary
        m["bestiary_frostgard_mamut_sepulcral_normal"] = R.drawable.bestiary_frostgard_mamut_sepulcral_normal
        m["bestiary_frostgard_mastin_nieve_champion"] = R.drawable.bestiary_frostgard_mastin_nieve_champion
        m["bestiary_frostgard_mastin_nieve_elite"] = R.drawable.bestiary_frostgard_mastin_nieve_elite
        m["bestiary_frostgard_mastin_nieve_legendary"] = R.drawable.bestiary_frostgard_mastin_nieve_legendary
        m["bestiary_frostgard_mastin_nieve_normal"] = R.drawable.bestiary_frostgard_mastin_nieve_normal
        m["bestiary_frostgard_oso_glaciar_antiguo_champion"] = R.drawable.bestiary_frostgard_oso_glaciar_antiguo_champion
        m["bestiary_frostgard_oso_glaciar_antiguo_elite"] = R.drawable.bestiary_frostgard_oso_glaciar_antiguo_elite
        m["bestiary_frostgard_oso_glaciar_antiguo_legendary"] = R.drawable.bestiary_frostgard_oso_glaciar_antiguo_legendary
        m["bestiary_frostgard_oso_glaciar_antiguo_normal"] = R.drawable.bestiary_frostgard_oso_glaciar_antiguo_normal
        m["bestiary_frostgard_serpiente_aguanieve_champion"] = R.drawable.bestiary_frostgard_serpiente_aguanieve_champion
        m["bestiary_frostgard_serpiente_aguanieve_elite"] = R.drawable.bestiary_frostgard_serpiente_aguanieve_elite
        m["bestiary_frostgard_serpiente_aguanieve_legendary"] = R.drawable.bestiary_frostgard_serpiente_aguanieve_legendary
        m["bestiary_frostgard_serpiente_aguanieve_normal"] = R.drawable.bestiary_frostgard_serpiente_aguanieve_normal
        m["bestiary_frostgard_skald_congelado_champion"] = R.drawable.bestiary_frostgard_skald_congelado_champion
        m["bestiary_frostgard_skald_congelado_elite"] = R.drawable.bestiary_frostgard_skald_congelado_elite
        m["bestiary_frostgard_skald_congelado_legendary"] = R.drawable.bestiary_frostgard_skald_congelado_legendary
        m["bestiary_frostgard_skald_congelado_normal"] = R.drawable.bestiary_frostgard_skald_congelado_normal
        m["bestiary_frostgard_vidente_congelada_champion"] = R.drawable.bestiary_frostgard_vidente_congelada_champion
        m["bestiary_frostgard_vidente_congelada_elite"] = R.drawable.bestiary_frostgard_vidente_congelada_elite
        m["bestiary_frostgard_vidente_congelada_legendary"] = R.drawable.bestiary_frostgard_vidente_congelada_legendary
        m["bestiary_frostgard_vidente_congelada_normal"] = R.drawable.bestiary_frostgard_vidente_congelada_normal
        m["bestiary_frostgard_yeti_cristalino_champion"] = R.drawable.bestiary_frostgard_yeti_cristalino_champion
        m["bestiary_frostgard_yeti_cristalino_elite"] = R.drawable.bestiary_frostgard_yeti_cristalino_elite
        m["bestiary_frostgard_yeti_cristalino_legendary"] = R.drawable.bestiary_frostgard_yeti_cristalino_legendary
        m["bestiary_frostgard_yeti_cristalino_normal"] = R.drawable.bestiary_frostgard_yeti_cristalino_normal
        m["bestiary_solaria_buitre_dorado_champion"] = R.drawable.bestiary_solaria_buitre_dorado_champion
        m["bestiary_solaria_buitre_dorado_elite"] = R.drawable.bestiary_solaria_buitre_dorado_elite
        m["bestiary_solaria_buitre_dorado_legendary"] = R.drawable.bestiary_solaria_buitre_dorado_legendary
        m["bestiary_solaria_buitre_dorado_normal"] = R.drawable.bestiary_solaria_buitre_dorado_normal
        m["bestiary_solaria_chacal_arena_champion"] = R.drawable.bestiary_solaria_chacal_arena_champion
        m["bestiary_solaria_chacal_arena_elite"] = R.drawable.bestiary_solaria_chacal_arena_elite
        m["bestiary_solaria_chacal_arena_legendary"] = R.drawable.bestiary_solaria_chacal_arena_legendary
        m["bestiary_solaria_chacal_arena_normal"] = R.drawable.bestiary_solaria_chacal_arena_normal
        m["bestiary_solaria_coloso_arenisca_champion"] = R.drawable.bestiary_solaria_coloso_arenisca_champion
        m["bestiary_solaria_coloso_arenisca_elite"] = R.drawable.bestiary_solaria_coloso_arenisca_elite
        m["bestiary_solaria_coloso_arenisca_legendary"] = R.drawable.bestiary_solaria_coloso_arenisca_legendary
        m["bestiary_solaria_coloso_arenisca_normal"] = R.drawable.bestiary_solaria_coloso_arenisca_normal
        m["bestiary_solaria_devorador_soles_champion"] = R.drawable.bestiary_solaria_devorador_soles_champion
        m["bestiary_solaria_devorador_soles_elite"] = R.drawable.bestiary_solaria_devorador_soles_elite
        m["bestiary_solaria_devorador_soles_legendary"] = R.drawable.bestiary_solaria_devorador_soles_legendary
        m["bestiary_solaria_devorador_soles_normal"] = R.drawable.bestiary_solaria_devorador_soles_normal
        m["bestiary_solaria_djinn_encadenado_champion"] = R.drawable.bestiary_solaria_djinn_encadenado_champion
        m["bestiary_solaria_djinn_encadenado_elite"] = R.drawable.bestiary_solaria_djinn_encadenado_elite
        m["bestiary_solaria_djinn_encadenado_legendary"] = R.drawable.bestiary_solaria_djinn_encadenado_legendary
        m["bestiary_solaria_djinn_encadenado_normal"] = R.drawable.bestiary_solaria_djinn_encadenado_normal
        m["bestiary_solaria_escarabajo_solar_champion"] = R.drawable.bestiary_solaria_escarabajo_solar_champion
        m["bestiary_solaria_escarabajo_solar_elite"] = R.drawable.bestiary_solaria_escarabajo_solar_elite
        m["bestiary_solaria_escarabajo_solar_legendary"] = R.drawable.bestiary_solaria_escarabajo_solar_legendary
        m["bestiary_solaria_escarabajo_solar_normal"] = R.drawable.bestiary_solaria_escarabajo_solar_normal
        m["bestiary_solaria_escorpion_ambar_champion"] = R.drawable.bestiary_solaria_escorpion_ambar_champion
        m["bestiary_solaria_escorpion_ambar_elite"] = R.drawable.bestiary_solaria_escorpion_ambar_elite
        m["bestiary_solaria_escorpion_ambar_legendary"] = R.drawable.bestiary_solaria_escorpion_ambar_legendary
        m["bestiary_solaria_escorpion_ambar_normal"] = R.drawable.bestiary_solaria_escorpion_ambar_normal
        m["bestiary_solaria_espejismo_viviente_champion"] = R.drawable.bestiary_solaria_espejismo_viviente_champion
        m["bestiary_solaria_espejismo_viviente_elite"] = R.drawable.bestiary_solaria_espejismo_viviente_elite
        m["bestiary_solaria_espejismo_viviente_legendary"] = R.drawable.bestiary_solaria_espejismo_viviente_legendary
        m["bestiary_solaria_espejismo_viviente_normal"] = R.drawable.bestiary_solaria_espejismo_viviente_normal
        m["bestiary_solaria_faraon_inmortal_champion"] = R.drawable.bestiary_solaria_faraon_inmortal_champion
    }

    private fun block2(m: MutableMap<String, Int>) {
        m["bestiary_solaria_faraon_inmortal_elite"] = R.drawable.bestiary_solaria_faraon_inmortal_elite
        m["bestiary_solaria_faraon_inmortal_legendary"] = R.drawable.bestiary_solaria_faraon_inmortal_legendary
        m["bestiary_solaria_faraon_inmortal_normal"] = R.drawable.bestiary_solaria_faraon_inmortal_normal
        m["bestiary_solaria_guardian_tumba_sellada_champion"] = R.drawable.bestiary_solaria_guardian_tumba_sellada_champion
        m["bestiary_solaria_guardian_tumba_sellada_elite"] = R.drawable.bestiary_solaria_guardian_tumba_sellada_elite
        m["bestiary_solaria_guardian_tumba_sellada_legendary"] = R.drawable.bestiary_solaria_guardian_tumba_sellada_legendary
        m["bestiary_solaria_guardian_tumba_sellada_normal"] = R.drawable.bestiary_solaria_guardian_tumba_sellada_normal
        m["bestiary_solaria_halcon_fuego_blanco_champion"] = R.drawable.bestiary_solaria_halcon_fuego_blanco_champion
        m["bestiary_solaria_halcon_fuego_blanco_elite"] = R.drawable.bestiary_solaria_halcon_fuego_blanco_elite
        m["bestiary_solaria_halcon_fuego_blanco_legendary"] = R.drawable.bestiary_solaria_halcon_fuego_blanco_legendary
        m["bestiary_solaria_halcon_fuego_blanco_normal"] = R.drawable.bestiary_solaria_halcon_fuego_blanco_normal
        m["bestiary_solaria_leon_alado_bronce_champion"] = R.drawable.bestiary_solaria_leon_alado_bronce_champion
        m["bestiary_solaria_leon_alado_bronce_elite"] = R.drawable.bestiary_solaria_leon_alado_bronce_elite
        m["bestiary_solaria_leon_alado_bronce_legendary"] = R.drawable.bestiary_solaria_leon_alado_bronce_legendary
        m["bestiary_solaria_leon_alado_bronce_normal"] = R.drawable.bestiary_solaria_leon_alado_bronce_normal
        m["bestiary_solaria_momia_dorada_champion"] = R.drawable.bestiary_solaria_momia_dorada_champion
        m["bestiary_solaria_momia_dorada_elite"] = R.drawable.bestiary_solaria_momia_dorada_elite
        m["bestiary_solaria_momia_dorada_legendary"] = R.drawable.bestiary_solaria_momia_dorada_legendary
        m["bestiary_solaria_momia_dorada_normal"] = R.drawable.bestiary_solaria_momia_dorada_normal
        m["bestiary_solaria_sacerdote_ceniza_solar_champion"] = R.drawable.bestiary_solaria_sacerdote_ceniza_solar_champion
        m["bestiary_solaria_sacerdote_ceniza_solar_elite"] = R.drawable.bestiary_solaria_sacerdote_ceniza_solar_elite
        m["bestiary_solaria_sacerdote_ceniza_solar_legendary"] = R.drawable.bestiary_solaria_sacerdote_ceniza_solar_legendary
        m["bestiary_solaria_sacerdote_ceniza_solar_normal"] = R.drawable.bestiary_solaria_sacerdote_ceniza_solar_normal
        m["bestiary_solaria_sacerdotisa_rashen_champion"] = R.drawable.bestiary_solaria_sacerdotisa_rashen_champion
        m["bestiary_solaria_sacerdotisa_rashen_elite"] = R.drawable.bestiary_solaria_sacerdotisa_rashen_elite
        m["bestiary_solaria_sacerdotisa_rashen_legendary"] = R.drawable.bestiary_solaria_sacerdotisa_rashen_legendary
        m["bestiary_solaria_sacerdotisa_rashen_normal"] = R.drawable.bestiary_solaria_sacerdotisa_rashen_normal
        m["bestiary_solaria_sepulturero_dunas_champion"] = R.drawable.bestiary_solaria_sepulturero_dunas_champion
        m["bestiary_solaria_sepulturero_dunas_elite"] = R.drawable.bestiary_solaria_sepulturero_dunas_elite
        m["bestiary_solaria_sepulturero_dunas_legendary"] = R.drawable.bestiary_solaria_sepulturero_dunas_legendary
        m["bestiary_solaria_sepulturero_dunas_normal"] = R.drawable.bestiary_solaria_sepulturero_dunas_normal
        m["bestiary_solaria_serafin_caido_champion"] = R.drawable.bestiary_solaria_serafin_caido_champion
        m["bestiary_solaria_serafin_caido_elite"] = R.drawable.bestiary_solaria_serafin_caido_elite
        m["bestiary_solaria_serafin_caido_legendary"] = R.drawable.bestiary_solaria_serafin_caido_legendary
        m["bestiary_solaria_serafin_caido_normal"] = R.drawable.bestiary_solaria_serafin_caido_normal
        m["bestiary_solaria_ushabti_sirviente_champion"] = R.drawable.bestiary_solaria_ushabti_sirviente_champion
        m["bestiary_solaria_ushabti_sirviente_elite"] = R.drawable.bestiary_solaria_ushabti_sirviente_elite
        m["bestiary_solaria_ushabti_sirviente_legendary"] = R.drawable.bestiary_solaria_ushabti_sirviente_legendary
        m["bestiary_solaria_ushabti_sirviente_normal"] = R.drawable.bestiary_solaria_ushabti_sirviente_normal
        m["cutscene_cleric_1784895944730"] = R.drawable.cutscene_cleric_1784895944730
        m["cutscene_mage_1784895923135"] = R.drawable.cutscene_mage_1784895923135
        m["cutscene_rogue_1784895933260"] = R.drawable.cutscene_rogue_1784895933260
        m["cutscene_warrior_1784895909697"] = R.drawable.cutscene_warrior_1784895909697
        m["dg01_boss_hobgoblin"] = R.drawable.dg01_boss_hobgoblin
        m["dg01_goblin_asesino"] = R.drawable.dg01_goblin_asesino
        m["dg01_goblin_capataz"] = R.drawable.dg01_goblin_capataz
        m["dg01_goblin_chaman"] = R.drawable.dg01_goblin_chaman
        m["dg01_goblin_cuadrillero"] = R.drawable.dg01_goblin_cuadrillero
        m["dg01_goblin_explorador"] = R.drawable.dg01_goblin_explorador
        m["dg01_goblin_fanatico"] = R.drawable.dg01_goblin_fanatico
        m["dg01_goblin_tactico"] = R.drawable.dg01_goblin_tactico
        m["dg01_goblin_tamborilero"] = R.drawable.dg01_goblin_tamborilero
        m["dg01_goblin_trampero"] = R.drawable.dg01_goblin_trampero
        m["dg02_asaltante_orco"] = R.drawable.dg02_asaltante_orco
        m["dg02_boss_rey_orco"] = R.drawable.dg02_boss_rey_orco
        m["dg02_capataz_de_hierro"] = R.drawable.dg02_capataz_de_hierro
        m["dg02_cazador_orco"] = R.drawable.dg02_cazador_orco
        m["dg02_chaman_orco"] = R.drawable.dg02_chaman_orco
        m["dg02_demoledor_orco"] = R.drawable.dg02_demoledor_orco
        m["dg02_gladiador_orco"] = R.drawable.dg02_gladiador_orco
        m["dg02_orco_berserker"] = R.drawable.dg02_orco_berserker
        m["dg02_tambor_de_guerra_orco"] = R.drawable.dg02_tambor_de_guerra_orco
        m["dg02_warg_rider"] = R.drawable.dg02_warg_rider
        m["dg03_boss_ladron_asesino"] = R.drawable.dg03_boss_ladron_asesino
        m["dg03_capitan_filo"] = R.drawable.dg03_capitan_filo
        m["dg03_envenenador_nocturno"] = R.drawable.dg03_envenenador_nocturno
        m["dg03_humano_ballestero"] = R.drawable.dg03_humano_ballestero
        m["dg03_humano_mercenario"] = R.drawable.dg03_humano_mercenario
        m["dg03_humano_sombra"] = R.drawable.dg03_humano_sombra
        m["dg03_infiltrador_humano"] = R.drawable.dg03_infiltrador_humano
        m["dg03_maton_de_callejon"] = R.drawable.dg03_maton_de_callejon
        m["dg03_pirata_del_puerto"] = R.drawable.dg03_pirata_del_puerto
        m["dg03_verdugo_de_las_sombras"] = R.drawable.dg03_verdugo_de_las_sombras
        m["dg04_bestia_alfa"] = R.drawable.dg04_bestia_alfa
        m["dg04_boss_rey_lobo_fenrir"] = R.drawable.dg04_boss_rey_lobo_fenrir
        m["dg04_centauro_de_guerra"] = R.drawable.dg04_centauro_de_guerra
        m["dg04_chacal_furioso"] = R.drawable.dg04_chacal_furioso
        m["dg04_chaman_bestial"] = R.drawable.dg04_chaman_bestial
        m["dg04_hombre_jabali"] = R.drawable.dg04_hombre_jabali
        m["dg04_licantropo_garra"] = R.drawable.dg04_licantropo_garra
        m["dg04_minotauro_feroz"] = R.drawable.dg04_minotauro_feroz
        m["dg04_oso_de_las_cavernas"] = R.drawable.dg04_oso_de_las_cavernas
        m["dg04_pantera_umbria"] = R.drawable.dg04_pantera_umbria
        m["dg05_boss_rey_del_oceano_neptuno"] = R.drawable.dg05_boss_rey_del_oceano_neptuno
        m["dg05_bruja_de_coral"] = R.drawable.dg05_bruja_de_coral
        m["dg05_devorador_de_fosas"] = R.drawable.dg05_devorador_de_fosas
        m["dg05_guardian_de_perlas"] = R.drawable.dg05_guardian_de_perlas
        m["dg05_leviatan_cazador"] = R.drawable.dg05_leviatan_cazador
        m["dg05_mago_de_mareas"] = R.drawable.dg05_mago_de_mareas
        m["dg05_naga_cazador"] = R.drawable.dg05_naga_cazador
        m["dg05_serpiente_abisal"] = R.drawable.dg05_serpiente_abisal
        m["dg05_sireno_de_coral"] = R.drawable.dg05_sireno_de_coral
        m["dg05_triton_de_las_profundidades"] = R.drawable.dg05_triton_de_las_profundidades
        m["dg06_alma_en_pena"] = R.drawable.dg06_alma_en_pena
        m["dg06_boss_vampiro_de_alto_nivel"] = R.drawable.dg06_boss_vampiro_de_alto_nivel
        m["dg06_caballero_de_la_muerte"] = R.drawable.dg06_caballero_de_la_muerte
        m["dg06_dragon_de_hueso"] = R.drawable.dg06_dragon_de_hueso
        m["dg06_espectro_de_hielo"] = R.drawable.dg06_espectro_de_hielo
        m["dg06_esqueleto_guerrero"] = R.drawable.dg06_esqueleto_guerrero
        m["dg06_lich_menor"] = R.drawable.dg06_lich_menor
        m["dg06_momia_ancestral"] = R.drawable.dg06_momia_ancestral
        m["dg06_necrofago_voraz"] = R.drawable.dg06_necrofago_voraz
        m["dg06_necromante_oscuro"] = R.drawable.dg06_necromante_oscuro
        m["dg07_alma_penante"] = R.drawable.dg07_alma_penante
        m["dg07_boss_rey_necromancer"] = R.drawable.dg07_boss_rey_necromancer
        m["dg07_espectro_del_vacio"] = R.drawable.dg07_espectro_del_vacio
        m["dg07_fuego_fatuo_ancestral"] = R.drawable.dg07_fuego_fatuo_ancestral
        m["dg07_furia_del_viento"] = R.drawable.dg07_furia_del_viento
        m["dg07_guardian_astral"] = R.drawable.dg07_guardian_astral
        m["dg07_lamento_de_las_sombras"] = R.drawable.dg07_lamento_de_las_sombras
        m["dg07_orbe_etereo"] = R.drawable.dg07_orbe_etereo
        m["dg07_poltergeist_furioso"] = R.drawable.dg07_poltergeist_furioso
        m["dg07_sombra_tormentosa"] = R.drawable.dg07_sombra_tormentosa
        m["dg08_asesino_anaconda"] = R.drawable.dg08_asesino_anaconda
        m["dg08_basilisco_menor"] = R.drawable.dg08_basilisco_menor
        m["dg08_boss_rey_serpiente_dragon"] = R.drawable.dg08_boss_rey_serpiente_dragon
        m["dg08_cascabel_de_muerte"] = R.drawable.dg08_cascabel_de_muerte
        m["dg08_devorador_de_veneno"] = R.drawable.dg08_devorador_de_veneno
        m["dg08_gorgona_de_hierro"] = R.drawable.dg08_gorgona_de_hierro
        m["dg08_guerrero_cobra"] = R.drawable.dg08_guerrero_cobra
        m["dg08_ilusionista_escamado"] = R.drawable.dg08_ilusionista_escamado
        m["dg08_nagani_guardian"] = R.drawable.dg08_nagani_guardian
        m["dg08_sacerdote_vibora"] = R.drawable.dg08_sacerdote_vibora
        m["dg09_automata_de_bronce"] = R.drawable.dg09_automata_de_bronce
        m["dg09_boss_igdrasil_el_cerebro_de_las_maquinas"] = R.drawable.dg09_boss_igdrasil_el_cerebro_de_las_maquinas
        m["dg09_celula_voltaica"] = R.drawable.dg09_celula_voltaica
        m["dg09_centinela_de_energia"] = R.drawable.dg09_centinela_de_energia
        m["dg09_coloso_mecanico"] = R.drawable.dg09_coloso_mecanico
        m["dg09_destructor_de_titanio"] = R.drawable.dg09_destructor_de_titanio
        m["dg09_dron_laser"] = R.drawable.dg09_dron_laser
        m["dg09_ejecutor_cibernetico"] = R.drawable.dg09_ejecutor_cibernetico
        m["dg09_golem_de_engranajes"] = R.drawable.dg09_golem_de_engranajes
        m["dg09_nucleo_de_plasma"] = R.drawable.dg09_nucleo_de_plasma
        m["dg10_boss_dragon_oscuro"] = R.drawable.dg10_boss_dragon_oscuro
        m["dg10_cria_de_dragon"] = R.drawable.dg10_cria_de_dragon
        m["dg10_dragon_ancestral"] = R.drawable.dg10_dragon_ancestral
        m["dg10_dragon_de_viento"] = R.drawable.dg10_dragon_de_viento
        m["dg10_dragon_dorado"] = R.drawable.dg10_dragon_dorado
        m["dg10_drake_caotico"] = R.drawable.dg10_drake_caotico
        m["dg10_drakoniano_de_magma"] = R.drawable.dg10_drakoniano_de_magma
        m["dg10_hidra_venenosa"] = R.drawable.dg10_hidra_venenosa
        m["dg10_wyrm_de_hielo"] = R.drawable.dg10_wyrm_de_hielo
        m["dg10_wyvern_de_fuego"] = R.drawable.dg10_wyvern_de_fuego
        m["dg11_angel_guerrero"] = R.drawable.dg11_angel_guerrero
        m["dg11_arcangel_menor"] = R.drawable.dg11_arcangel_menor
        m["dg11_boss_archicreador_seraph"] = R.drawable.dg11_boss_archicreador_seraph
        m["dg11_guardian_celestial"] = R.drawable.dg11_guardian_celestial
        m["dg11_juez_del_firmamento"] = R.drawable.dg11_juez_del_firmamento
        m["dg11_querubin_de_guerra"] = R.drawable.dg11_querubin_de_guerra
        m["dg11_sacerdote_estelar"] = R.drawable.dg11_sacerdote_estelar
        m["dg11_sentinela_sagrado"] = R.drawable.dg11_sentinela_sagrado
        m["dg11_serafin_de_luz"] = R.drawable.dg11_serafin_de_luz
        m["dg11_valquiria_de_cristal"] = R.drawable.dg11_valquiria_de_cristal
        m["dg12_basilisco_marino"] = R.drawable.dg12_basilisco_marino
        m["dg12_boss_leviatan_cthulhu"] = R.drawable.dg12_boss_leviatan_cthulhu
        m["dg12_calamar_estigio"] = R.drawable.dg12_calamar_estigio
        m["dg12_devorador_de_almas"] = R.drawable.dg12_devorador_de_almas
        m["dg12_engendro_del_maelstrom"] = R.drawable.dg12_engendro_del_maelstrom
        m["dg12_guardian_de_la_fosa"] = R.drawable.dg12_guardian_de_la_fosa
        m["dg12_horror_de_la_fosa"] = R.drawable.dg12_horror_de_la_fosa
        m["dg12_kraken_de_sangre"] = R.drawable.dg12_kraken_de_sangre
        m["dg12_sombra_estigia"] = R.drawable.dg12_sombra_estigia
        m["dg12_triton_maldito"] = R.drawable.dg12_triton_maldito
        m["dg13_begimo_runico"] = R.drawable.dg13_begimo_runico
        m["dg13_boss_forjador_supremo_aethel"] = R.drawable.dg13_boss_forjador_supremo_aethel
        m["dg13_centinela_cosmico"] = R.drawable.dg13_centinela_cosmico
        m["dg13_coloso_de_bronce"] = R.drawable.dg13_coloso_de_bronce
        m["dg13_destructor_estelar"] = R.drawable.dg13_destructor_estelar
        m["dg13_elemental_de_plasma"] = R.drawable.dg13_elemental_de_plasma
        m["dg13_escultor_del_caos"] = R.drawable.dg13_escultor_del_caos
        m["dg13_golem_de_obsidiana"] = R.drawable.dg13_golem_de_obsidiana
        m["dg13_guardian_de_la_forja"] = R.drawable.dg13_guardian_de_la_forja
        m["dg13_minotauro_de_titanio"] = R.drawable.dg13_minotauro_de_titanio
        m["dg14_archidemonio_de_azufre"] = R.drawable.dg14_archidemonio_de_azufre
        m["dg14_balrog_de_las_cenizas"] = R.drawable.dg14_balrog_de_las_cenizas
        m["dg14_belfegor_de_la_gula"] = R.drawable.dg14_belfegor_de_la_gula
        m["dg14_boss_lucifer_senor_del_inframundo"] = R.drawable.dg14_boss_lucifer_senor_del_inframundo
        m["dg14_cerbero_de_lava"] = R.drawable.dg14_cerbero_de_lava
        m["dg14_gargola_de_inframundo"] = R.drawable.dg14_gargola_de_inframundo
        m["dg14_incubo_de_la_sombra"] = R.drawable.dg14_incubo_de_la_sombra
        m["dg14_mammon_del_infortunio"] = R.drawable.dg14_mammon_del_infortunio
        m["dg14_senor_de_la_tormenta"] = R.drawable.dg14_senor_de_la_tormenta
        m["dg14_sucubo_infernal"] = R.drawable.dg14_sucubo_infernal
        m["dg15_aniquilador_estelar"] = R.drawable.dg15_aniquilador_estelar
        m["dg15_boss_sombra_del_dios_olvidado"] = R.drawable.dg15_boss_sombra_del_dios_olvidado
        m["dg15_centinela_del_vacio"] = R.drawable.dg15_centinela_del_vacio
        m["dg15_distorsion_cuantica"] = R.drawable.dg15_distorsion_cuantica
        m["dg15_ente_incorporeo"] = R.drawable.dg15_ente_incorporeo
        m["dg15_espectro_infinito"] = R.drawable.dg15_espectro_infinito
        m["dg15_espiritu_del_abismo"] = R.drawable.dg15_espiritu_del_abismo
        m["dg15_herrero_del_olvido"] = R.drawable.dg15_herrero_del_olvido
        m["dg15_terror_nocturno"] = R.drawable.dg15_terror_nocturno
        m["dg15_vertice_de_sombras"] = R.drawable.dg15_vertice_de_sombras
        m["dg16_avatar_de_la_realidad"] = R.drawable.dg16_avatar_de_la_realidad
        m["dg16_boss_ouroboros_el_eterno"] = R.drawable.dg16_boss_ouroboros_el_eterno
        m["dg16_dragon_de_la_creacion"] = R.drawable.dg16_dragon_de_la_creacion
        m["dg16_guardian_del_tiempo"] = R.drawable.dg16_guardian_del_tiempo
        m["dg16_heraldo_del_destino"] = R.drawable.dg16_heraldo_del_destino
        m["dg16_nebula_destructora"] = R.drawable.dg16_nebula_destructora
        m["dg16_soberano_cosmico"] = R.drawable.dg16_soberano_cosmico
    }

    private fun block3(m: MutableMap<String, Int>) {
        m["dg16_sombra_del_big_bang"] = R.drawable.dg16_sombra_del_big_bang
        m["dg16_titan_del_espacio"] = R.drawable.dg16_titan_del_espacio
        m["dg16_vertice_infinito"] = R.drawable.dg16_vertice_infinito
        m["empty_bestiary"] = R.drawable.empty_bestiary
        m["empty_generic"] = R.drawable.empty_generic
        m["empty_inventory"] = R.drawable.empty_inventory
        m["empty_pets"] = R.drawable.empty_pets
        m["empty_quests"] = R.drawable.empty_quests
        m["empty_shop"] = R.drawable.empty_shop
        m["enemy_anubis_1784850895657"] = R.drawable.enemy_anubis_1784850895657
        m["enemy_archangel_1784850912318"] = R.drawable.enemy_archangel_1784850912318
        m["enemy_automaton_1784850938702"] = R.drawable.enemy_automaton_1784850938702
        m["enemy_bandit_1784850826788"] = R.drawable.enemy_bandit_1784850826788
        m["enemy_basilisk_1784850958621"] = R.drawable.enemy_basilisk_1784850958621
        m["enemy_cultist_1784850844974"] = R.drawable.enemy_cultist_1784850844974
        m["enemy_demon_1784903246195"] = R.drawable.enemy_demon_1784903246195
        m["enemy_dragon_1784850948333"] = R.drawable.enemy_dragon_1784850948333
        m["enemy_elemental_1784850835033"] = R.drawable.enemy_elemental_1784850835033
        m["enemy_goblin_1784850794614"] = R.drawable.enemy_goblin_1784850794614
        m["enemy_kraken_1784903268006"] = R.drawable.enemy_kraken_1784903268006
        m["enemy_lich_1784850885522"] = R.drawable.enemy_lich_1784850885522
        m["enemy_minotaur_1784903256639"] = R.drawable.enemy_minotaur_1784903256639
        m["enemy_mummy_1784850903429"] = R.drawable.enemy_mummy_1784850903429
        m["enemy_naga_1784850928739"] = R.drawable.enemy_naga_1784850928739
        m["enemy_orc_1784850920168"] = R.drawable.enemy_orc_1784850920168
        m["enemy_scorpion_1784850968611"] = R.drawable.enemy_scorpion_1784850968611
        m["enemy_spectre_1784850809472"] = R.drawable.enemy_spectre_1784850809472
        m["enemy_treant_1784850817186"] = R.drawable.enemy_treant_1784850817186
        m["enemy_vampire_1784903236424"] = R.drawable.enemy_vampire_1784903236424
        m["enemy_witch_1784850877826"] = R.drawable.enemy_witch_1784850877826
        m["enemy_wolf_1784850801847"] = R.drawable.enemy_wolf_1784850801847
        m["enemy_yeti_1784850855217"] = R.drawable.enemy_yeti_1784850855217
        m["enemy_zombie_1784850868957"] = R.drawable.enemy_zombie_1784850868957
        m["gothic_skill_bar_bg_1784670759745"] = R.drawable.gothic_skill_bar_bg_1784670759745
        m["img_boss_dark_dragon_1784674128719"] = R.drawable.img_boss_dark_dragon_1784674128719
        m["img_boss_high_vampire_1784674139269"] = R.drawable.img_boss_high_vampire_1784674139269
        m["img_boss_hobgoblin_1784674116743"] = R.drawable.img_boss_hobgoblin_1784674116743
        m["img_boss_yggdrasil_machine_1784674150126"] = R.drawable.img_boss_yggdrasil_machine_1784674150126
        m["img_combat_btn_blue_1784604624972"] = R.drawable.img_combat_btn_blue_1784604624972
        m["img_combat_btn_red_1784604609410"] = R.drawable.img_combat_btn_red_1784604609410
        m["img_dungeon_door_1784674104372"] = R.drawable.img_dungeon_door_1784674104372
        m["img_enemy_boss_1784386985144"] = R.drawable.img_enemy_boss_1784386985144
        m["img_enemy_mud_golem_1784386930907"] = R.drawable.img_enemy_mud_golem_1784386930907
        m["img_enemy_ogre_1784386944311"] = R.drawable.img_enemy_ogre_1784386944311
        m["img_enemy_spectre_1784386971041"] = R.drawable.img_enemy_spectre_1784386971041
        m["img_enemy_spider_1784386956688"] = R.drawable.img_enemy_spider_1784386956688
        m["img_evo_clerigo_1_1784901558922"] = R.drawable.img_evo_clerigo_1_1784901558922
        m["img_evo_clerigo_2_1784901568583"] = R.drawable.img_evo_clerigo_2_1784901568583
        m["img_evo_clerigo_3_1784901581001"] = R.drawable.img_evo_clerigo_3_1784901581001
        m["img_evo_guerrero_1_1784901448962"] = R.drawable.img_evo_guerrero_1_1784901448962
        m["img_evo_guerrero_2_1784901460545"] = R.drawable.img_evo_guerrero_2_1784901460545
        m["img_evo_guerrero_3_1784901472272"] = R.drawable.img_evo_guerrero_3_1784901472272
        m["img_evo_mago_1_1784901481413"] = R.drawable.img_evo_mago_1_1784901481413
        m["img_evo_mago_2_1784901492327"] = R.drawable.img_evo_mago_2_1784901492327
        m["img_evo_mago_3_1784901503996"] = R.drawable.img_evo_mago_3_1784901503996
        m["img_evo_picaro_1_1784901516704"] = R.drawable.img_evo_picaro_1_1784901516704
        m["img_evo_picaro_2_1784901529027"] = R.drawable.img_evo_picaro_2_1784901529027
        m["img_evo_picaro_3_1784901545572"] = R.drawable.img_evo_picaro_3_1784901545572
        m["img_food_bestial_1785008135868"] = R.drawable.img_food_bestial_1785008135868
        m["img_food_celestial_1785008169473"] = R.drawable.img_food_celestial_1785008169473
        m["img_food_dragon_1785008159001"] = R.drawable.img_food_dragon_1785008159001
        m["img_food_mistica_1785008148513"] = R.drawable.img_food_mistica_1785008148513
        m["img_hero_advanced_clerigo_1784856159204"] = R.drawable.img_hero_advanced_clerigo_1784856159204
        m["img_hero_advanced_guerrero_1784856127764"] = R.drawable.img_hero_advanced_guerrero_1784856127764
        m["img_hero_advanced_mago_1784856138389"] = R.drawable.img_hero_advanced_mago_1784856138389
        m["img_hero_advanced_picaro_1784856148296"] = R.drawable.img_hero_advanced_picaro_1784856148296
        m["img_item_boots_1784658239207"] = R.drawable.img_item_boots_1784658239207
        m["img_item_dagger_1784593567531"] = R.drawable.img_item_dagger_1784593567531
        m["img_item_earring_1784658263366"] = R.drawable.img_item_earring_1784658263366
        m["img_item_gloves_1784658226142"] = R.drawable.img_item_gloves_1784658226142
        m["img_item_helmet_1784658214656"] = R.drawable.img_item_helmet_1784658214656
        m["img_item_plate_1784593577913"] = R.drawable.img_item_plate_1784593577913
        m["img_item_potion_1784593618142"] = R.drawable.img_item_potion_1784593618142
        m["img_item_relic_1784658251007"] = R.drawable.img_item_relic_1784658251007
        m["img_item_ring_1784593597914"] = R.drawable.img_item_ring_1784593597914
        m["img_item_robe_1784593587883"] = R.drawable.img_item_robe_1784593587883
        m["img_item_shield_1784593608106"] = R.drawable.img_item_shield_1784593608106
        m["img_item_staff_1784593558118"] = R.drawable.img_item_staff_1784593558118
        m["img_item_sword_1784593548868"] = R.drawable.img_item_sword_1784593548868
        m["img_item_wings_1784658202673"] = R.drawable.img_item_wings_1784658202673
        m["img_mat_cuero_1784901594849"] = R.drawable.img_mat_cuero_1784901594849
        m["img_mat_diamond_inf_1784901652591"] = R.drawable.img_mat_diamond_inf_1784901652591
        m["img_mat_dragondskin_1784901640557"] = R.drawable.img_mat_dragondskin_1784901640557
        m["img_mat_hierro_1784901606157"] = R.drawable.img_mat_hierro_1784901606157
        m["img_mat_oro_1784901617574"] = R.drawable.img_mat_oro_1784901617574
        m["img_mat_platino_1784901629448"] = R.drawable.img_mat_platino_1784901629448
        m["img_medieval_map"] = R.drawable.img_medieval_map
        m["img_pet_behemoth_vacio_1785007703732"] = R.drawable.img_pet_behemoth_vacio_1785007703732
        m["img_pet_dragon_sombras_1785007642225"] = R.drawable.img_pet_dragon_sombras_1785007642225
        m["img_pet_fenix_cosmico_1785007631115"] = R.drawable.img_pet_fenix_cosmico_1785007631115
        m["img_pet_gato_estelar_1785007661828"] = R.drawable.img_pet_gato_estelar_1785007661828
        m["img_pet_grifo_dorado_1785007680820"] = R.drawable.img_pet_grifo_dorado_1785007680820
        m["img_pet_lobo_celestial_1785007652368"] = R.drawable.img_pet_lobo_celestial_1785007652368
        m["img_pet_serpiente_astral_1785007692823"] = R.drawable.img_pet_serpiente_astral_1785007692823
        m["img_pet_titan_cristal_1785007671322"] = R.drawable.img_pet_titan_cristal_1785007671322
        m["img_portrait_cleric"] = R.drawable.img_portrait_cleric
        m["img_portrait_elfo_clerigo_1784507380857"] = R.drawable.img_portrait_elfo_clerigo_1784507380857
        m["img_portrait_elfo_guerrero_1784507353139"] = R.drawable.img_portrait_elfo_guerrero_1784507353139
        m["img_portrait_elfo_mago_1784507362479"] = R.drawable.img_portrait_elfo_mago_1784507362479
        m["img_portrait_elfo_picaro_1784507372605"] = R.drawable.img_portrait_elfo_picaro_1784507372605
        m["img_portrait_enano_clerigo_1784507424242"] = R.drawable.img_portrait_enano_clerigo_1784507424242
        m["img_portrait_enano_guerrero_1784507393580"] = R.drawable.img_portrait_enano_guerrero_1784507393580
        m["img_portrait_enano_mago_1784507404164"] = R.drawable.img_portrait_enano_mago_1784507404164
        m["img_portrait_enano_picaro_1784507414525"] = R.drawable.img_portrait_enano_picaro_1784507414525
        m["img_portrait_humano_clerigo_1784507343785"] = R.drawable.img_portrait_humano_clerigo_1784507343785
        m["img_portrait_humano_guerrero_1784507309143"] = R.drawable.img_portrait_humano_guerrero_1784507309143
        m["img_portrait_humano_mago_1784507318980"] = R.drawable.img_portrait_humano_mago_1784507318980
        m["img_portrait_humano_picaro_1784507327963"] = R.drawable.img_portrait_humano_picaro_1784507327963
        m["img_portrait_mage"] = R.drawable.img_portrait_mage
        m["img_portrait_orco_clerigo_1784507461591"] = R.drawable.img_portrait_orco_clerigo_1784507461591
        m["img_portrait_orco_guerrero_1784507433308"] = R.drawable.img_portrait_orco_guerrero_1784507433308
        m["img_portrait_orco_mago_1784507441780"] = R.drawable.img_portrait_orco_mago_1784507441780
        m["img_portrait_orco_picaro_1784507451567"] = R.drawable.img_portrait_orco_picaro_1784507451567
        m["img_portrait_rogue"] = R.drawable.img_portrait_rogue
        m["img_portrait_warrior"] = R.drawable.img_portrait_warrior
        m["img_shop_merchant_1784605357079"] = R.drawable.img_shop_merchant_1784605357079
        m["img_talents_bg_1784603912942"] = R.drawable.img_talents_bg_1784603912942
        m["img_tile_chest_1784470917774"] = R.drawable.img_tile_chest_1784470917774
        m["img_tile_enemy_1784470940695"] = R.drawable.img_tile_enemy_1784470940695
        m["img_tile_grass_1784470894787"] = R.drawable.img_tile_grass_1784470894787
        m["img_tile_obstacle_1784470907788"] = R.drawable.img_tile_obstacle_1784470907788
        m["img_tile_shrine_1784470929381"] = R.drawable.img_tile_shrine_1784470929381
        m["img_warcraft_bottom_bar_1784669353873"] = R.drawable.img_warcraft_bottom_bar_1784669353873
        m["img_world_map_banner"] = R.drawable.img_world_map_banner
        m["mat_ancient_relic"] = R.drawable.mat_ancient_relic
        m["mat_anima_shard"] = R.drawable.mat_anima_shard
        m["mat_blood_gem"] = R.drawable.mat_blood_gem
        m["mat_crystal"] = R.drawable.mat_crystal
        m["mat_forge_ember"] = R.drawable.mat_forge_ember
        m["mat_herbs"] = R.drawable.mat_herbs
        m["mat_mystic_silk"] = R.drawable.mat_mystic_silk
        m["mat_phoenix_feather"] = R.drawable.mat_phoenix_feather
        m["mat_pure_crystal"] = R.drawable.mat_pure_crystal
        m["mat_sealed_key"] = R.drawable.mat_sealed_key
        m["mat_shadow_essence"] = R.drawable.mat_shadow_essence
        m["mat_wood"] = R.drawable.mat_wood
        m["merchant_stall_banner_1784845825754"] = R.drawable.merchant_stall_banner_1784845825754
        m["pet_ave_trueno_s1"] = R.drawable.pet_ave_trueno_s1
        m["pet_ave_trueno_s2"] = R.drawable.pet_ave_trueno_s2
        m["pet_ave_trueno_s3"] = R.drawable.pet_ave_trueno_s3
        m["pet_basilisco_jade_s1"] = R.drawable.pet_basilisco_jade_s1
        m["pet_basilisco_jade_s2"] = R.drawable.pet_basilisco_jade_s2
        m["pet_basilisco_jade_s3"] = R.drawable.pet_basilisco_jade_s3
        m["pet_behemoth_vacio_s1"] = R.drawable.pet_behemoth_vacio_s1
        m["pet_behemoth_vacio_s2"] = R.drawable.pet_behemoth_vacio_s2
        m["pet_behemoth_vacio_s3"] = R.drawable.pet_behemoth_vacio_s3
        m["pet_buho_runico_s1"] = R.drawable.pet_buho_runico_s1
        m["pet_buho_runico_s2"] = R.drawable.pet_buho_runico_s2
        m["pet_buho_runico_s3"] = R.drawable.pet_buho_runico_s3
        m["pet_custodio_umbral_s1"] = R.drawable.pet_custodio_umbral_s1
        m["pet_custodio_umbral_s2"] = R.drawable.pet_custodio_umbral_s2
        m["pet_custodio_umbral_s3"] = R.drawable.pet_custodio_umbral_s3
        m["pet_dragon_sombras_s1"] = R.drawable.pet_dragon_sombras_s1
        m["pet_dragon_sombras_s2"] = R.drawable.pet_dragon_sombras_s2
        m["pet_dragon_sombras_s3"] = R.drawable.pet_dragon_sombras_s3
        m["pet_escarabajo_coraza_s1"] = R.drawable.pet_escarabajo_coraza_s1
        m["pet_escarabajo_coraza_s2"] = R.drawable.pet_escarabajo_coraza_s2
        m["pet_escarabajo_coraza_s3"] = R.drawable.pet_escarabajo_coraza_s3
        m["pet_espectro_aurora_s1"] = R.drawable.pet_espectro_aurora_s1
        m["pet_espectro_aurora_s2"] = R.drawable.pet_espectro_aurora_s2
        m["pet_espectro_aurora_s3"] = R.drawable.pet_espectro_aurora_s3
        m["pet_fenix_cosmico_s1"] = R.drawable.pet_fenix_cosmico_s1
        m["pet_fenix_cosmico_s2"] = R.drawable.pet_fenix_cosmico_s2
        m["pet_fenix_cosmico_s3"] = R.drawable.pet_fenix_cosmico_s3
        m["pet_gato_estelar_s1"] = R.drawable.pet_gato_estelar_s1
        m["pet_gato_estelar_s2"] = R.drawable.pet_gato_estelar_s2
        m["pet_gato_estelar_s3"] = R.drawable.pet_gato_estelar_s3
        m["pet_golem_musgo_s1"] = R.drawable.pet_golem_musgo_s1
        m["pet_golem_musgo_s2"] = R.drawable.pet_golem_musgo_s2
        m["pet_golem_musgo_s3"] = R.drawable.pet_golem_musgo_s3
        m["pet_grifo_dorado_s1"] = R.drawable.pet_grifo_dorado_s1
        m["pet_grifo_dorado_s2"] = R.drawable.pet_grifo_dorado_s2
        m["pet_grifo_dorado_s3"] = R.drawable.pet_grifo_dorado_s3
        m["pet_kirin_tormenta_s1"] = R.drawable.pet_kirin_tormenta_s1
        m["pet_kirin_tormenta_s2"] = R.drawable.pet_kirin_tormenta_s2
        m["pet_kirin_tormenta_s3"] = R.drawable.pet_kirin_tormenta_s3
        m["pet_leviatan_bolsillo_s1"] = R.drawable.pet_leviatan_bolsillo_s1
        m["pet_leviatan_bolsillo_s2"] = R.drawable.pet_leviatan_bolsillo_s2
        m["pet_leviatan_bolsillo_s3"] = R.drawable.pet_leviatan_bolsillo_s3
        m["pet_liebre_lunar_s1"] = R.drawable.pet_liebre_lunar_s1
        m["pet_liebre_lunar_s2"] = R.drawable.pet_liebre_lunar_s2
        m["pet_liebre_lunar_s3"] = R.drawable.pet_liebre_lunar_s3
        m["pet_lobo_celestial_s1"] = R.drawable.pet_lobo_celestial_s1
        m["pet_lobo_celestial_s2"] = R.drawable.pet_lobo_celestial_s2
        m["pet_lobo_celestial_s3"] = R.drawable.pet_lobo_celestial_s3
        m["pet_manticora_menor_s1"] = R.drawable.pet_manticora_menor_s1
        m["pet_manticora_menor_s2"] = R.drawable.pet_manticora_menor_s2
        m["pet_manticora_menor_s3"] = R.drawable.pet_manticora_menor_s3
        m["pet_ouroboros_menor_s1"] = R.drawable.pet_ouroboros_menor_s1
        m["pet_ouroboros_menor_s2"] = R.drawable.pet_ouroboros_menor_s2
        m["pet_ouroboros_menor_s3"] = R.drawable.pet_ouroboros_menor_s3
        m["pet_quimera_cristal_s1"] = R.drawable.pet_quimera_cristal_s1
        m["pet_quimera_cristal_s2"] = R.drawable.pet_quimera_cristal_s2
        m["pet_quimera_cristal_s3"] = R.drawable.pet_quimera_cristal_s3
        m["pet_salamandra_forja_s1"] = R.drawable.pet_salamandra_forja_s1
        m["pet_salamandra_forja_s2"] = R.drawable.pet_salamandra_forja_s2
        m["pet_salamandra_forja_s3"] = R.drawable.pet_salamandra_forja_s3
        m["pet_serpiente_astral_s1"] = R.drawable.pet_serpiente_astral_s1
        m["pet_serpiente_astral_s2"] = R.drawable.pet_serpiente_astral_s2
        m["pet_serpiente_astral_s3"] = R.drawable.pet_serpiente_astral_s3
    }

    private fun block4(m: MutableMap<String, Int>) {
        m["pet_titan_cristal_s1"] = R.drawable.pet_titan_cristal_s1
        m["pet_titan_cristal_s2"] = R.drawable.pet_titan_cristal_s2
        m["pet_titan_cristal_s3"] = R.drawable.pet_titan_cristal_s3
        m["pet_wyvern_crepuscular_s1"] = R.drawable.pet_wyvern_crepuscular_s1
        m["pet_wyvern_crepuscular_s2"] = R.drawable.pet_wyvern_crepuscular_s2
        m["pet_wyvern_crepuscular_s3"] = R.drawable.pet_wyvern_crepuscular_s3
        m["pet_zorro_ceniza_s1"] = R.drawable.pet_zorro_ceniza_s1
        m["pet_zorro_ceniza_s2"] = R.drawable.pet_zorro_ceniza_s2
        m["pet_zorro_ceniza_s3"] = R.drawable.pet_zorro_ceniza_s3
        m["potion_furia"] = R.drawable.potion_furia
        m["potion_mayor"] = R.drawable.potion_mayor
        m["potion_menor"] = R.drawable.potion_menor
        m["potion_piedra"] = R.drawable.potion_piedra
        m["potion_regen"] = R.drawable.potion_regen
        m["potion_sombra"] = R.drawable.potion_sombra
        m["skill_c_1"] = R.drawable.skill_c_1
        m["skill_c_2"] = R.drawable.skill_c_2
        m["skill_g_1"] = R.drawable.skill_g_1
        m["skill_g_2"] = R.drawable.skill_g_2
        m["skill_m_1"] = R.drawable.skill_m_1
        m["skill_m_2"] = R.drawable.skill_m_2
        m["skill_p_1"] = R.drawable.skill_p_1
        m["skill_p_2"] = R.drawable.skill_p_2
        m["skill_sk_adv_cleric"] = R.drawable.skill_sk_adv_cleric
        m["skill_sk_adv_mage"] = R.drawable.skill_sk_adv_mage
        m["skill_sk_adv_rogue"] = R.drawable.skill_sk_adv_rogue
        m["skill_sk_adv_warrior"] = R.drawable.skill_sk_adv_warrior
        m["table_adiestramiento"] = R.drawable.table_adiestramiento
        m["table_excavacion"] = R.drawable.table_excavacion
        m["table_ganzua"] = R.drawable.table_ganzua
        m["table_glifos"] = R.drawable.table_glifos
        m["table_vigilia"] = R.drawable.table_vigilia
        m["table_yunque"] = R.drawable.table_yunque
        m["talent_elf_arc1"] = R.drawable.talent_elf_arc1
        m["talent_elf_arc10"] = R.drawable.talent_elf_arc10
        m["talent_elf_arc11"] = R.drawable.talent_elf_arc11
        m["talent_elf_arc12"] = R.drawable.talent_elf_arc12
        m["talent_elf_arc2"] = R.drawable.talent_elf_arc2
        m["talent_elf_arc3"] = R.drawable.talent_elf_arc3
        m["talent_elf_arc4"] = R.drawable.talent_elf_arc4
        m["talent_elf_arc5"] = R.drawable.talent_elf_arc5
        m["talent_elf_arc6"] = R.drawable.talent_elf_arc6
        m["talent_elf_arc7"] = R.drawable.talent_elf_arc7
        m["talent_elf_arc8"] = R.drawable.talent_elf_arc8
        m["talent_elf_arc9"] = R.drawable.talent_elf_arc9
        m["talent_elf_arm1"] = R.drawable.talent_elf_arm1
        m["talent_elf_arm10"] = R.drawable.talent_elf_arm10
        m["talent_elf_arm11"] = R.drawable.talent_elf_arm11
        m["talent_elf_arm2"] = R.drawable.talent_elf_arm2
        m["talent_elf_arm3"] = R.drawable.talent_elf_arm3
        m["talent_elf_arm4"] = R.drawable.talent_elf_arm4
        m["talent_elf_arm5"] = R.drawable.talent_elf_arm5
        m["talent_elf_arm6"] = R.drawable.talent_elf_arm6
        m["talent_elf_arm7"] = R.drawable.talent_elf_arm7
        m["talent_elf_arm8"] = R.drawable.talent_elf_arm8
        m["talent_elf_arm9"] = R.drawable.talent_elf_arm9
        m["talent_elf_bes1"] = R.drawable.talent_elf_bes1
        m["talent_elf_bes10"] = R.drawable.talent_elf_bes10
        m["talent_elf_bes11"] = R.drawable.talent_elf_bes11
        m["talent_elf_bes2"] = R.drawable.talent_elf_bes2
        m["talent_elf_bes3"] = R.drawable.talent_elf_bes3
        m["talent_elf_bes4"] = R.drawable.talent_elf_bes4
        m["talent_elf_bes5"] = R.drawable.talent_elf_bes5
        m["talent_elf_bes6"] = R.drawable.talent_elf_bes6
        m["talent_elf_bes7"] = R.drawable.talent_elf_bes7
        m["talent_elf_bes8"] = R.drawable.talent_elf_bes8
        m["talent_elf_bes9"] = R.drawable.talent_elf_bes9
        m["talent_elf_def1"] = R.drawable.talent_elf_def1
        m["talent_elf_def10"] = R.drawable.talent_elf_def10
        m["talent_elf_def11"] = R.drawable.talent_elf_def11
        m["talent_elf_def2"] = R.drawable.talent_elf_def2
        m["talent_elf_def3"] = R.drawable.talent_elf_def3
        m["talent_elf_def4"] = R.drawable.talent_elf_def4
        m["talent_elf_def5"] = R.drawable.talent_elf_def5
        m["talent_elf_def6"] = R.drawable.talent_elf_def6
        m["talent_elf_def7"] = R.drawable.talent_elf_def7
        m["talent_elf_def8"] = R.drawable.talent_elf_def8
        m["talent_elf_def9"] = R.drawable.talent_elf_def9
        m["talent_elf_ev1a"] = R.drawable.talent_elf_ev1a
        m["talent_elf_ev1b"] = R.drawable.talent_elf_ev1b
        m["talent_elf_ev1c"] = R.drawable.talent_elf_ev1c
        m["talent_elf_ev1d"] = R.drawable.talent_elf_ev1d
        m["talent_elf_ev2a"] = R.drawable.talent_elf_ev2a
        m["talent_elf_ev2b"] = R.drawable.talent_elf_ev2b
        m["talent_elf_ev2c"] = R.drawable.talent_elf_ev2c
        m["talent_elf_ev2d"] = R.drawable.talent_elf_ev2d
        m["talent_elf_ev3a"] = R.drawable.talent_elf_ev3a
        m["talent_elf_ev3b"] = R.drawable.talent_elf_ev3b
        m["talent_elf_ev3c"] = R.drawable.talent_elf_ev3c
        m["talent_elf_ev3d"] = R.drawable.talent_elf_ev3d
        m["talent_elf_for1"] = R.drawable.talent_elf_for1
        m["talent_elf_for10"] = R.drawable.talent_elf_for10
        m["talent_elf_for11"] = R.drawable.talent_elf_for11
        m["talent_elf_for2"] = R.drawable.talent_elf_for2
        m["talent_elf_for3"] = R.drawable.talent_elf_for3
        m["talent_elf_for4"] = R.drawable.talent_elf_for4
        m["talent_elf_for5"] = R.drawable.talent_elf_for5
        m["talent_elf_for6"] = R.drawable.talent_elf_for6
        m["talent_elf_for7"] = R.drawable.talent_elf_for7
        m["talent_elf_for8"] = R.drawable.talent_elf_for8
        m["talent_elf_for9"] = R.drawable.talent_elf_for9
        m["talent_elf_leg1"] = R.drawable.talent_elf_leg1
        m["talent_elf_leg10"] = R.drawable.talent_elf_leg10
        m["talent_elf_leg11"] = R.drawable.talent_elf_leg11
        m["talent_elf_leg2"] = R.drawable.talent_elf_leg2
        m["talent_elf_leg3"] = R.drawable.talent_elf_leg3
        m["talent_elf_leg4"] = R.drawable.talent_elf_leg4
        m["talent_elf_leg5"] = R.drawable.talent_elf_leg5
        m["talent_elf_leg6"] = R.drawable.talent_elf_leg6
        m["talent_elf_leg7"] = R.drawable.talent_elf_leg7
        m["talent_elf_leg8"] = R.drawable.talent_elf_leg8
        m["talent_elf_leg9"] = R.drawable.talent_elf_leg9
        m["talent_elf_san1"] = R.drawable.talent_elf_san1
        m["talent_elf_san10"] = R.drawable.talent_elf_san10
        m["talent_elf_san11"] = R.drawable.talent_elf_san11
        m["talent_elf_san2"] = R.drawable.talent_elf_san2
        m["talent_elf_san3"] = R.drawable.talent_elf_san3
        m["talent_elf_san4"] = R.drawable.talent_elf_san4
        m["talent_elf_san5"] = R.drawable.talent_elf_san5
        m["talent_elf_san6"] = R.drawable.talent_elf_san6
        m["talent_elf_san7"] = R.drawable.talent_elf_san7
        m["talent_elf_san8"] = R.drawable.talent_elf_san8
        m["talent_elf_san9"] = R.drawable.talent_elf_san9
        m["talent_elf_som1"] = R.drawable.talent_elf_som1
        m["talent_elf_som10"] = R.drawable.talent_elf_som10
        m["talent_elf_som2"] = R.drawable.talent_elf_som2
        m["talent_elf_som3"] = R.drawable.talent_elf_som3
        m["talent_elf_som4"] = R.drawable.talent_elf_som4
        m["talent_elf_som5"] = R.drawable.talent_elf_som5
        m["talent_elf_som6"] = R.drawable.talent_elf_som6
        m["talent_elf_som7"] = R.drawable.talent_elf_som7
        m["talent_elf_som8"] = R.drawable.talent_elf_som8
        m["talent_elf_som9"] = R.drawable.talent_elf_som9
        m["talent_ena_arc1"] = R.drawable.talent_ena_arc1
        m["talent_ena_arc10"] = R.drawable.talent_ena_arc10
        m["talent_ena_arc2"] = R.drawable.talent_ena_arc2
        m["talent_ena_arc3"] = R.drawable.talent_ena_arc3
        m["talent_ena_arc4"] = R.drawable.talent_ena_arc4
        m["talent_ena_arc5"] = R.drawable.talent_ena_arc5
        m["talent_ena_arc6"] = R.drawable.talent_ena_arc6
        m["talent_ena_arc7"] = R.drawable.talent_ena_arc7
        m["talent_ena_arc8"] = R.drawable.talent_ena_arc8
        m["talent_ena_arc9"] = R.drawable.talent_ena_arc9
        m["talent_ena_arm1"] = R.drawable.talent_ena_arm1
        m["talent_ena_arm10"] = R.drawable.talent_ena_arm10
        m["talent_ena_arm11"] = R.drawable.talent_ena_arm11
        m["talent_ena_arm12"] = R.drawable.talent_ena_arm12
        m["talent_ena_arm2"] = R.drawable.talent_ena_arm2
        m["talent_ena_arm3"] = R.drawable.talent_ena_arm3
        m["talent_ena_arm4"] = R.drawable.talent_ena_arm4
        m["talent_ena_arm5"] = R.drawable.talent_ena_arm5
        m["talent_ena_arm6"] = R.drawable.talent_ena_arm6
        m["talent_ena_arm7"] = R.drawable.talent_ena_arm7
        m["talent_ena_arm8"] = R.drawable.talent_ena_arm8
        m["talent_ena_arm9"] = R.drawable.talent_ena_arm9
        m["talent_ena_bes1"] = R.drawable.talent_ena_bes1
        m["talent_ena_bes10"] = R.drawable.talent_ena_bes10
        m["talent_ena_bes11"] = R.drawable.talent_ena_bes11
        m["talent_ena_bes12"] = R.drawable.talent_ena_bes12
        m["talent_ena_bes2"] = R.drawable.talent_ena_bes2
        m["talent_ena_bes3"] = R.drawable.talent_ena_bes3
        m["talent_ena_bes4"] = R.drawable.talent_ena_bes4
        m["talent_ena_bes5"] = R.drawable.talent_ena_bes5
        m["talent_ena_bes6"] = R.drawable.talent_ena_bes6
        m["talent_ena_bes7"] = R.drawable.talent_ena_bes7
        m["talent_ena_bes8"] = R.drawable.talent_ena_bes8
        m["talent_ena_bes9"] = R.drawable.talent_ena_bes9
        m["talent_ena_def1"] = R.drawable.talent_ena_def1
        m["talent_ena_def10"] = R.drawable.talent_ena_def10
        m["talent_ena_def11"] = R.drawable.talent_ena_def11
        m["talent_ena_def12"] = R.drawable.talent_ena_def12
        m["talent_ena_def2"] = R.drawable.talent_ena_def2
        m["talent_ena_def3"] = R.drawable.talent_ena_def3
        m["talent_ena_def4"] = R.drawable.talent_ena_def4
        m["talent_ena_def5"] = R.drawable.talent_ena_def5
        m["talent_ena_def6"] = R.drawable.talent_ena_def6
        m["talent_ena_def7"] = R.drawable.talent_ena_def7
        m["talent_ena_def8"] = R.drawable.talent_ena_def8
        m["talent_ena_def9"] = R.drawable.talent_ena_def9
        m["talent_ena_ev1a"] = R.drawable.talent_ena_ev1a
        m["talent_ena_ev1b"] = R.drawable.talent_ena_ev1b
        m["talent_ena_ev1c"] = R.drawable.talent_ena_ev1c
        m["talent_ena_ev1d"] = R.drawable.talent_ena_ev1d
        m["talent_ena_ev2a"] = R.drawable.talent_ena_ev2a
        m["talent_ena_ev2b"] = R.drawable.talent_ena_ev2b
        m["talent_ena_ev2c"] = R.drawable.talent_ena_ev2c
        m["talent_ena_ev2d"] = R.drawable.talent_ena_ev2d
        m["talent_ena_ev3a"] = R.drawable.talent_ena_ev3a
        m["talent_ena_ev3b"] = R.drawable.talent_ena_ev3b
        m["talent_ena_ev3c"] = R.drawable.talent_ena_ev3c
        m["talent_ena_ev3d"] = R.drawable.talent_ena_ev3d
        m["talent_ena_for1"] = R.drawable.talent_ena_for1
        m["talent_ena_for10"] = R.drawable.talent_ena_for10
        m["talent_ena_for2"] = R.drawable.talent_ena_for2
        m["talent_ena_for3"] = R.drawable.talent_ena_for3
        m["talent_ena_for4"] = R.drawable.talent_ena_for4
        m["talent_ena_for5"] = R.drawable.talent_ena_for5
        m["talent_ena_for6"] = R.drawable.talent_ena_for6
        m["talent_ena_for7"] = R.drawable.talent_ena_for7
        m["talent_ena_for8"] = R.drawable.talent_ena_for8
    }

    private fun block5(m: MutableMap<String, Int>) {
        m["talent_ena_for9"] = R.drawable.talent_ena_for9
        m["talent_ena_leg1"] = R.drawable.talent_ena_leg1
        m["talent_ena_leg10"] = R.drawable.talent_ena_leg10
        m["talent_ena_leg11"] = R.drawable.talent_ena_leg11
        m["talent_ena_leg12"] = R.drawable.talent_ena_leg12
        m["talent_ena_leg2"] = R.drawable.talent_ena_leg2
        m["talent_ena_leg3"] = R.drawable.talent_ena_leg3
        m["talent_ena_leg4"] = R.drawable.talent_ena_leg4
        m["talent_ena_leg5"] = R.drawable.talent_ena_leg5
        m["talent_ena_leg6"] = R.drawable.talent_ena_leg6
        m["talent_ena_leg7"] = R.drawable.talent_ena_leg7
        m["talent_ena_leg8"] = R.drawable.talent_ena_leg8
        m["talent_ena_leg9"] = R.drawable.talent_ena_leg9
        m["talent_ena_san1"] = R.drawable.talent_ena_san1
        m["talent_ena_san10"] = R.drawable.talent_ena_san10
        m["talent_ena_san2"] = R.drawable.talent_ena_san2
        m["talent_ena_san3"] = R.drawable.talent_ena_san3
        m["talent_ena_san4"] = R.drawable.talent_ena_san4
        m["talent_ena_san5"] = R.drawable.talent_ena_san5
        m["talent_ena_san6"] = R.drawable.talent_ena_san6
        m["talent_ena_san7"] = R.drawable.talent_ena_san7
        m["talent_ena_san8"] = R.drawable.talent_ena_san8
        m["talent_ena_san9"] = R.drawable.talent_ena_san9
        m["talent_ena_som1"] = R.drawable.talent_ena_som1
        m["talent_ena_som10"] = R.drawable.talent_ena_som10
        m["talent_ena_som2"] = R.drawable.talent_ena_som2
        m["talent_ena_som3"] = R.drawable.talent_ena_som3
        m["talent_ena_som4"] = R.drawable.talent_ena_som4
        m["talent_ena_som5"] = R.drawable.talent_ena_som5
        m["talent_ena_som6"] = R.drawable.talent_ena_som6
        m["talent_ena_som7"] = R.drawable.talent_ena_som7
        m["talent_ena_som8"] = R.drawable.talent_ena_som8
        m["talent_ena_som9"] = R.drawable.talent_ena_som9
        m["talent_hum_arc1"] = R.drawable.talent_hum_arc1
        m["talent_hum_arc10"] = R.drawable.talent_hum_arc10
        m["talent_hum_arc11"] = R.drawable.talent_hum_arc11
        m["talent_hum_arc2"] = R.drawable.talent_hum_arc2
        m["talent_hum_arc3"] = R.drawable.talent_hum_arc3
        m["talent_hum_arc4"] = R.drawable.talent_hum_arc4
        m["talent_hum_arc5"] = R.drawable.talent_hum_arc5
        m["talent_hum_arc6"] = R.drawable.talent_hum_arc6
        m["talent_hum_arc7"] = R.drawable.talent_hum_arc7
        m["talent_hum_arc8"] = R.drawable.talent_hum_arc8
        m["talent_hum_arc9"] = R.drawable.talent_hum_arc9
        m["talent_hum_arm1"] = R.drawable.talent_hum_arm1
        m["talent_hum_arm10"] = R.drawable.talent_hum_arm10
        m["talent_hum_arm11"] = R.drawable.talent_hum_arm11
        m["talent_hum_arm2"] = R.drawable.talent_hum_arm2
        m["talent_hum_arm3"] = R.drawable.talent_hum_arm3
        m["talent_hum_arm4"] = R.drawable.talent_hum_arm4
        m["talent_hum_arm5"] = R.drawable.talent_hum_arm5
        m["talent_hum_arm6"] = R.drawable.talent_hum_arm6
        m["talent_hum_arm7"] = R.drawable.talent_hum_arm7
        m["talent_hum_arm8"] = R.drawable.talent_hum_arm8
        m["talent_hum_arm9"] = R.drawable.talent_hum_arm9
        m["talent_hum_bes1"] = R.drawable.talent_hum_bes1
        m["talent_hum_bes10"] = R.drawable.talent_hum_bes10
        m["talent_hum_bes11"] = R.drawable.talent_hum_bes11
        m["talent_hum_bes2"] = R.drawable.talent_hum_bes2
        m["talent_hum_bes3"] = R.drawable.talent_hum_bes3
        m["talent_hum_bes4"] = R.drawable.talent_hum_bes4
        m["talent_hum_bes5"] = R.drawable.talent_hum_bes5
        m["talent_hum_bes6"] = R.drawable.talent_hum_bes6
        m["talent_hum_bes7"] = R.drawable.talent_hum_bes7
        m["talent_hum_bes8"] = R.drawable.talent_hum_bes8
        m["talent_hum_bes9"] = R.drawable.talent_hum_bes9
        m["talent_hum_def1"] = R.drawable.talent_hum_def1
        m["talent_hum_def10"] = R.drawable.talent_hum_def10
        m["talent_hum_def11"] = R.drawable.talent_hum_def11
        m["talent_hum_def2"] = R.drawable.talent_hum_def2
        m["talent_hum_def3"] = R.drawable.talent_hum_def3
        m["talent_hum_def4"] = R.drawable.talent_hum_def4
        m["talent_hum_def5"] = R.drawable.talent_hum_def5
        m["talent_hum_def6"] = R.drawable.talent_hum_def6
        m["talent_hum_def7"] = R.drawable.talent_hum_def7
        m["talent_hum_def8"] = R.drawable.talent_hum_def8
        m["talent_hum_def9"] = R.drawable.talent_hum_def9
        m["talent_hum_ev1a"] = R.drawable.talent_hum_ev1a
        m["talent_hum_ev1b"] = R.drawable.talent_hum_ev1b
        m["talent_hum_ev1c"] = R.drawable.talent_hum_ev1c
        m["talent_hum_ev1d"] = R.drawable.talent_hum_ev1d
        m["talent_hum_ev2a"] = R.drawable.talent_hum_ev2a
        m["talent_hum_ev2b"] = R.drawable.talent_hum_ev2b
        m["talent_hum_ev2c"] = R.drawable.talent_hum_ev2c
        m["talent_hum_ev2d"] = R.drawable.talent_hum_ev2d
        m["talent_hum_ev3a"] = R.drawable.talent_hum_ev3a
        m["talent_hum_ev3b"] = R.drawable.talent_hum_ev3b
        m["talent_hum_ev3c"] = R.drawable.talent_hum_ev3c
        m["talent_hum_ev3d"] = R.drawable.talent_hum_ev3d
        m["talent_hum_for1"] = R.drawable.talent_hum_for1
        m["talent_hum_for10"] = R.drawable.talent_hum_for10
        m["talent_hum_for11"] = R.drawable.talent_hum_for11
        m["talent_hum_for2"] = R.drawable.talent_hum_for2
        m["talent_hum_for3"] = R.drawable.talent_hum_for3
        m["talent_hum_for4"] = R.drawable.talent_hum_for4
        m["talent_hum_for5"] = R.drawable.talent_hum_for5
        m["talent_hum_for6"] = R.drawable.talent_hum_for6
        m["talent_hum_for7"] = R.drawable.talent_hum_for7
        m["talent_hum_for8"] = R.drawable.talent_hum_for8
        m["talent_hum_for9"] = R.drawable.talent_hum_for9
        m["talent_hum_leg1"] = R.drawable.talent_hum_leg1
        m["talent_hum_leg10"] = R.drawable.talent_hum_leg10
        m["talent_hum_leg11"] = R.drawable.talent_hum_leg11
        m["talent_hum_leg2"] = R.drawable.talent_hum_leg2
        m["talent_hum_leg3"] = R.drawable.talent_hum_leg3
        m["talent_hum_leg4"] = R.drawable.talent_hum_leg4
        m["talent_hum_leg5"] = R.drawable.talent_hum_leg5
        m["talent_hum_leg6"] = R.drawable.talent_hum_leg6
        m["talent_hum_leg7"] = R.drawable.talent_hum_leg7
        m["talent_hum_leg8"] = R.drawable.talent_hum_leg8
        m["talent_hum_leg9"] = R.drawable.talent_hum_leg9
        m["talent_hum_san1"] = R.drawable.talent_hum_san1
        m["talent_hum_san10"] = R.drawable.talent_hum_san10
        m["talent_hum_san11"] = R.drawable.talent_hum_san11
        m["talent_hum_san2"] = R.drawable.talent_hum_san2
        m["talent_hum_san3"] = R.drawable.talent_hum_san3
        m["talent_hum_san4"] = R.drawable.talent_hum_san4
        m["talent_hum_san5"] = R.drawable.talent_hum_san5
        m["talent_hum_san6"] = R.drawable.talent_hum_san6
        m["talent_hum_san7"] = R.drawable.talent_hum_san7
        m["talent_hum_san8"] = R.drawable.talent_hum_san8
        m["talent_hum_san9"] = R.drawable.talent_hum_san9
        m["talent_hum_som1"] = R.drawable.talent_hum_som1
        m["talent_hum_som10"] = R.drawable.talent_hum_som10
        m["talent_hum_som11"] = R.drawable.talent_hum_som11
        m["talent_hum_som2"] = R.drawable.talent_hum_som2
        m["talent_hum_som3"] = R.drawable.talent_hum_som3
        m["talent_hum_som4"] = R.drawable.talent_hum_som4
        m["talent_hum_som5"] = R.drawable.talent_hum_som5
        m["talent_hum_som6"] = R.drawable.talent_hum_som6
        m["talent_hum_som7"] = R.drawable.talent_hum_som7
        m["talent_hum_som8"] = R.drawable.talent_hum_som8
        m["talent_hum_som9"] = R.drawable.talent_hum_som9
        m["talent_orc_arc1"] = R.drawable.talent_orc_arc1
        m["talent_orc_arc2"] = R.drawable.talent_orc_arc2
        m["talent_orc_arc3"] = R.drawable.talent_orc_arc3
        m["talent_orc_arc4"] = R.drawable.talent_orc_arc4
        m["talent_orc_arc5"] = R.drawable.talent_orc_arc5
        m["talent_orc_arc6"] = R.drawable.talent_orc_arc6
        m["talent_orc_arm1"] = R.drawable.talent_orc_arm1
        m["talent_orc_arm10"] = R.drawable.talent_orc_arm10
        m["talent_orc_arm11"] = R.drawable.talent_orc_arm11
        m["talent_orc_arm12"] = R.drawable.talent_orc_arm12
        m["talent_orc_arm13"] = R.drawable.talent_orc_arm13
        m["talent_orc_arm14"] = R.drawable.talent_orc_arm14
        m["talent_orc_arm15"] = R.drawable.talent_orc_arm15
        m["talent_orc_arm16"] = R.drawable.talent_orc_arm16
        m["talent_orc_arm2"] = R.drawable.talent_orc_arm2
        m["talent_orc_arm3"] = R.drawable.talent_orc_arm3
        m["talent_orc_arm4"] = R.drawable.talent_orc_arm4
        m["talent_orc_arm5"] = R.drawable.talent_orc_arm5
        m["talent_orc_arm6"] = R.drawable.talent_orc_arm6
        m["talent_orc_arm7"] = R.drawable.talent_orc_arm7
        m["talent_orc_arm8"] = R.drawable.talent_orc_arm8
        m["talent_orc_arm9"] = R.drawable.talent_orc_arm9
        m["talent_orc_bes1"] = R.drawable.talent_orc_bes1
        m["talent_orc_bes10"] = R.drawable.talent_orc_bes10
        m["talent_orc_bes11"] = R.drawable.talent_orc_bes11
        m["talent_orc_bes2"] = R.drawable.talent_orc_bes2
        m["talent_orc_bes3"] = R.drawable.talent_orc_bes3
        m["talent_orc_bes4"] = R.drawable.talent_orc_bes4
        m["talent_orc_bes5"] = R.drawable.talent_orc_bes5
        m["talent_orc_bes6"] = R.drawable.talent_orc_bes6
        m["talent_orc_bes7"] = R.drawable.talent_orc_bes7
        m["talent_orc_bes8"] = R.drawable.talent_orc_bes8
        m["talent_orc_bes9"] = R.drawable.talent_orc_bes9
        m["talent_orc_def1"] = R.drawable.talent_orc_def1
        m["talent_orc_def10"] = R.drawable.talent_orc_def10
        m["talent_orc_def11"] = R.drawable.talent_orc_def11
        m["talent_orc_def2"] = R.drawable.talent_orc_def2
        m["talent_orc_def3"] = R.drawable.talent_orc_def3
        m["talent_orc_def4"] = R.drawable.talent_orc_def4
        m["talent_orc_def5"] = R.drawable.talent_orc_def5
        m["talent_orc_def6"] = R.drawable.talent_orc_def6
        m["talent_orc_def7"] = R.drawable.talent_orc_def7
        m["talent_orc_def8"] = R.drawable.talent_orc_def8
        m["talent_orc_def9"] = R.drawable.talent_orc_def9
        m["talent_orc_ev1a"] = R.drawable.talent_orc_ev1a
        m["talent_orc_ev1b"] = R.drawable.talent_orc_ev1b
        m["talent_orc_ev1c"] = R.drawable.talent_orc_ev1c
        m["talent_orc_ev1d"] = R.drawable.talent_orc_ev1d
        m["talent_orc_ev2a"] = R.drawable.talent_orc_ev2a
        m["talent_orc_ev2b"] = R.drawable.talent_orc_ev2b
        m["talent_orc_ev2c"] = R.drawable.talent_orc_ev2c
        m["talent_orc_ev2d"] = R.drawable.talent_orc_ev2d
        m["talent_orc_ev3a"] = R.drawable.talent_orc_ev3a
        m["talent_orc_ev3b"] = R.drawable.talent_orc_ev3b
        m["talent_orc_ev3c"] = R.drawable.talent_orc_ev3c
        m["talent_orc_ev3d"] = R.drawable.talent_orc_ev3d
        m["talent_orc_for1"] = R.drawable.talent_orc_for1
        m["talent_orc_for10"] = R.drawable.talent_orc_for10
        m["talent_orc_for2"] = R.drawable.talent_orc_for2
        m["talent_orc_for3"] = R.drawable.talent_orc_for3
        m["talent_orc_for4"] = R.drawable.talent_orc_for4
        m["talent_orc_for5"] = R.drawable.talent_orc_for5
        m["talent_orc_for6"] = R.drawable.talent_orc_for6
        m["talent_orc_for7"] = R.drawable.talent_orc_for7
        m["talent_orc_for8"] = R.drawable.talent_orc_for8
        m["talent_orc_for9"] = R.drawable.talent_orc_for9
        m["talent_orc_leg1"] = R.drawable.talent_orc_leg1
    }

    private fun block6(m: MutableMap<String, Int>) {
        m["talent_orc_leg2"] = R.drawable.talent_orc_leg2
        m["talent_orc_leg3"] = R.drawable.talent_orc_leg3
        m["talent_orc_leg4"] = R.drawable.talent_orc_leg4
        m["talent_orc_leg5"] = R.drawable.talent_orc_leg5
        m["talent_orc_leg6"] = R.drawable.talent_orc_leg6
        m["talent_orc_leg7"] = R.drawable.talent_orc_leg7
        m["talent_orc_leg8"] = R.drawable.talent_orc_leg8
        m["talent_orc_leg9"] = R.drawable.talent_orc_leg9
        m["talent_orc_san1"] = R.drawable.talent_orc_san1
        m["talent_orc_san10"] = R.drawable.talent_orc_san10
        m["talent_orc_san11"] = R.drawable.talent_orc_san11
        m["talent_orc_san12"] = R.drawable.talent_orc_san12
        m["talent_orc_san13"] = R.drawable.talent_orc_san13
        m["talent_orc_san14"] = R.drawable.talent_orc_san14
        m["talent_orc_san15"] = R.drawable.talent_orc_san15
        m["talent_orc_san2"] = R.drawable.talent_orc_san2
        m["talent_orc_san3"] = R.drawable.talent_orc_san3
        m["talent_orc_san4"] = R.drawable.talent_orc_san4
        m["talent_orc_san5"] = R.drawable.talent_orc_san5
        m["talent_orc_san6"] = R.drawable.talent_orc_san6
        m["talent_orc_san7"] = R.drawable.talent_orc_san7
        m["talent_orc_san8"] = R.drawable.talent_orc_san8
        m["talent_orc_san9"] = R.drawable.talent_orc_san9
        m["talent_orc_som1"] = R.drawable.talent_orc_som1
        m["talent_orc_som10"] = R.drawable.talent_orc_som10
        m["talent_orc_som2"] = R.drawable.talent_orc_som2
        m["talent_orc_som3"] = R.drawable.talent_orc_som3
        m["talent_orc_som4"] = R.drawable.talent_orc_som4
        m["talent_orc_som5"] = R.drawable.talent_orc_som5
        m["talent_orc_som6"] = R.drawable.talent_orc_som6
        m["talent_orc_som7"] = R.drawable.talent_orc_som7
        m["talent_orc_som8"] = R.drawable.talent_orc_som8
        m["talent_orc_som9"] = R.drawable.talent_orc_som9
        m["talent_tree_banner_1784843563984"] = R.drawable.talent_tree_banner_1784843563984
        m["wandering_merchant_1784845746333"] = R.drawable.wandering_merchant_1784845746333
        m["warcraft3_hud_panel_1784669998817"] = R.drawable.warcraft3_hud_panel_1784669998817
    }

    /** Lamina por clave exacta, o null si no hay ninguna con ese nombre. */
    fun of(key: String): Int? = INDEX[key]

    /**
     * Los enemigos de calabozo no salen del bestiario: se nombran en
     * DUNGEONS_LIST. Este mapa traduce (calabozo, nombre) a su lamina para que
     * los nueve subjefes de un mismo calabozo dejen de compartir retrato.
     */
    private val DUNGEON: Map<String, String> = mapOf(
        "10|BOSS" to "dg10_boss_dragon_oscuro",
        "10|Cría de Dragón" to "dg10_cria_de_dragon",
        "10|Dragón Ancestral" to "dg10_dragon_ancestral",
        "10|Dragón Dorado" to "dg10_dragon_dorado",
        "10|Dragón de Viento" to "dg10_dragon_de_viento",
        "10|Drake Caótico" to "dg10_drake_caotico",
        "10|Drakoniano de Magma" to "dg10_drakoniano_de_magma",
        "10|Hidra Venenosa" to "dg10_hidra_venenosa",
        "10|Wyrm de Hielo" to "dg10_wyrm_de_hielo",
        "10|Wyvern de Fuego" to "dg10_wyvern_de_fuego",
        "11|Arcángel Menor" to "dg11_arcangel_menor",
        "11|BOSS" to "dg11_boss_archicreador_seraph",
        "11|Guardián Celestial" to "dg11_guardian_celestial",
        "11|Juez del Firmamento" to "dg11_juez_del_firmamento",
        "11|Querubín de Guerra" to "dg11_querubin_de_guerra",
        "11|Sacerdote Estelar" to "dg11_sacerdote_estelar",
        "11|Sentinela Sagrado" to "dg11_sentinela_sagrado",
        "11|Serafín de Luz" to "dg11_serafin_de_luz",
        "11|Valquiria de Cristal" to "dg11_valquiria_de_cristal",
        "11|Ángel Guerrero" to "dg11_angel_guerrero",
        "12|BOSS" to "dg12_boss_leviatan_cthulhu",
        "12|Basilisco Marino" to "dg12_basilisco_marino",
        "12|Calamar Estigio" to "dg12_calamar_estigio",
        "12|Devorador de Almas" to "dg12_devorador_de_almas",
        "12|Engendro del Maelstrom" to "dg12_engendro_del_maelstrom",
        "12|Guardián de la Fosa" to "dg12_guardian_de_la_fosa",
        "12|Horror de la Fosa" to "dg12_horror_de_la_fosa",
        "12|Kraken de Sangre" to "dg12_kraken_de_sangre",
        "12|Sombra Estigia" to "dg12_sombra_estigia",
        "12|Tritón Maldito" to "dg12_triton_maldito",
        "13|BOSS" to "dg13_boss_forjador_supremo_aethel",
        "13|Bégimo Rúnico" to "dg13_begimo_runico",
        "13|Centinela Cósmico" to "dg13_centinela_cosmico",
        "13|Coloso de Bronce" to "dg13_coloso_de_bronce",
        "13|Destructor Estelar" to "dg13_destructor_estelar",
        "13|Elemental de Plasma" to "dg13_elemental_de_plasma",
        "13|Escultor del Caos" to "dg13_escultor_del_caos",
        "13|Guardián de la Forja" to "dg13_guardian_de_la_forja",
        "13|Gólem de Obsidiana" to "dg13_golem_de_obsidiana",
        "13|Minotauro de Titanio" to "dg13_minotauro_de_titanio",
        "14|Archidemonio de Azufre" to "dg14_archidemonio_de_azufre",
        "14|BOSS" to "dg14_boss_lucifer_senor_del_inframundo",
        "14|Balrog de las Cenizas" to "dg14_balrog_de_las_cenizas",
        "14|Belfegor de la Gula" to "dg14_belfegor_de_la_gula",
        "14|Cerbero de Lava" to "dg14_cerbero_de_lava",
        "14|Gárgola de Inframundo" to "dg14_gargola_de_inframundo",
        "14|Mammon del Infortunio" to "dg14_mammon_del_infortunio",
        "14|Señor de la Tormenta" to "dg14_senor_de_la_tormenta",
        "14|Súcubo Infernal" to "dg14_sucubo_infernal",
        "14|Íncubo de la Sombra" to "dg14_incubo_de_la_sombra",
        "15|Aniquilador Estelar" to "dg15_aniquilador_estelar",
        "15|BOSS" to "dg15_boss_sombra_del_dios_olvidado",
        "15|Centinela del Vacío" to "dg15_centinela_del_vacio",
        "15|Distorsión Cuántica" to "dg15_distorsion_cuantica",
        "15|Ente Incorpóreo" to "dg15_ente_incorporeo",
        "15|Espectro Infinito" to "dg15_espectro_infinito",
        "15|Espíritu del Abismo" to "dg15_espiritu_del_abismo",
        "15|Herrero del Olvido" to "dg15_herrero_del_olvido",
        "15|Terror Nocturno" to "dg15_terror_nocturno",
        "15|Vértice de Sombras" to "dg15_vertice_de_sombras",
        "16|Avatar de la Realidad" to "dg16_avatar_de_la_realidad",
        "16|BOSS" to "dg16_boss_ouroboros_el_eterno",
        "16|Dragón de la Creación" to "dg16_dragon_de_la_creacion",
        "16|Guardián del Tiempo" to "dg16_guardian_del_tiempo",
        "16|Heraldo del Destino" to "dg16_heraldo_del_destino",
        "16|Nébula Destructora" to "dg16_nebula_destructora",
        "16|Soberano Cósmico" to "dg16_soberano_cosmico",
        "16|Sombra del Big Bang" to "dg16_sombra_del_big_bang",
        "16|Titán del Espacio" to "dg16_titan_del_espacio",
        "16|Vértice Infinito" to "dg16_vertice_infinito",
        "1|BOSS" to "dg01_boss_hobgoblin",
        "1|Goblin Asesino" to "dg01_goblin_asesino",
        "1|Goblin Capataz" to "dg01_goblin_capataz",
        "1|Goblin Chamán" to "dg01_goblin_chaman",
        "1|Goblin Cuadrillero" to "dg01_goblin_cuadrillero",
        "1|Goblin Explorador" to "dg01_goblin_explorador",
        "1|Goblin Fanático" to "dg01_goblin_fanatico",
        "1|Goblin Tamborilero" to "dg01_goblin_tamborilero",
        "1|Goblin Trampero" to "dg01_goblin_trampero",
        "1|Goblin Táctico" to "dg01_goblin_tactico",
        "2|Asaltante Orco" to "dg02_asaltante_orco",
        "2|BOSS" to "dg02_boss_rey_orco",
        "2|Capataz de Hierro" to "dg02_capataz_de_hierro",
        "2|Cazador Orco" to "dg02_cazador_orco",
        "2|Chamán Orco" to "dg02_chaman_orco",
        "2|Demoledor Orco" to "dg02_demoledor_orco",
        "2|Gladiador Orco" to "dg02_gladiador_orco",
        "2|Orco Berserker" to "dg02_orco_berserker",
        "2|Tambor de Guerra Orco" to "dg02_tambor_de_guerra_orco",
        "2|Warg Rider" to "dg02_warg_rider",
        "3|BOSS" to "dg03_boss_ladron_asesino",
        "3|Capitán Filo" to "dg03_capitan_filo",
        "3|Envenenador Nocturno" to "dg03_envenenador_nocturno",
        "3|Humano Ballestero" to "dg03_humano_ballestero",
        "3|Humano Mercenario" to "dg03_humano_mercenario",
        "3|Humano Sombra" to "dg03_humano_sombra",
        "3|Infiltrador Humano" to "dg03_infiltrador_humano",
        "3|Matón de Callejón" to "dg03_maton_de_callejon",
        "3|Pirata del Puerto" to "dg03_pirata_del_puerto",
        "3|Verdugo de las Sombras" to "dg03_verdugo_de_las_sombras",
        "4|BOSS" to "dg04_boss_rey_lobo_fenrir",
        "4|Bestia Alfa" to "dg04_bestia_alfa",
        "4|Centauro de Guerra" to "dg04_centauro_de_guerra",
        "4|Chacal Furioso" to "dg04_chacal_furioso",
        "4|Chamán Bestial" to "dg04_chaman_bestial",
        "4|Hombre Jabalí" to "dg04_hombre_jabali",
        "4|Licántropo Garra" to "dg04_licantropo_garra",
        "4|Minotauro Feroz" to "dg04_minotauro_feroz",
        "4|Oso de las Cavernas" to "dg04_oso_de_las_cavernas",
        "4|Pantera Umbría" to "dg04_pantera_umbria",
        "5|BOSS" to "dg05_boss_rey_del_oceano_neptuno",
        "5|Bruja de Coral" to "dg05_bruja_de_coral",
        "5|Devorador de Fosas" to "dg05_devorador_de_fosas",
        "5|Guardián de Perlas" to "dg05_guardian_de_perlas",
        "5|Leviatán Cazador" to "dg05_leviatan_cazador",
        "5|Mago de Mareas" to "dg05_mago_de_mareas",
        "5|Naga Cazador" to "dg05_naga_cazador",
        "5|Serpiente Abisal" to "dg05_serpiente_abisal",
        "5|Sireno de Coral" to "dg05_sireno_de_coral",
        "5|Tritón de las Profundidades" to "dg05_triton_de_las_profundidades",
        "6|Alma en Pena" to "dg06_alma_en_pena",
        "6|BOSS" to "dg06_boss_vampiro_de_alto_nivel",
        "6|Caballero de la Muerte" to "dg06_caballero_de_la_muerte",
        "6|Dragón de Hueso" to "dg06_dragon_de_hueso",
        "6|Espectro de Hielo" to "dg06_espectro_de_hielo",
        "6|Esqueleto Guerrero" to "dg06_esqueleto_guerrero",
        "6|Lich Menor" to "dg06_lich_menor",
        "6|Momia Ancestral" to "dg06_momia_ancestral",
        "6|Necromante Oscuro" to "dg06_necromante_oscuro",
        "6|Necrófago Voraz" to "dg06_necrofago_voraz",
        "7|Alma Penante" to "dg07_alma_penante",
        "7|BOSS" to "dg07_boss_rey_necromancer",
        "7|Espectro del Vacío" to "dg07_espectro_del_vacio",
        "7|Fuego Fatuo Ancestral" to "dg07_fuego_fatuo_ancestral",
        "7|Furia del Viento" to "dg07_furia_del_viento",
        "7|Guardián Astral" to "dg07_guardian_astral",
        "7|Lamento de las Sombras" to "dg07_lamento_de_las_sombras",
        "7|Orbe Etéreo" to "dg07_orbe_etereo",
        "7|Poltergeist Furioso" to "dg07_poltergeist_furioso",
        "7|Sombra Tormentosa" to "dg07_sombra_tormentosa",
        "8|Asesino Anaconda" to "dg08_asesino_anaconda",
        "8|BOSS" to "dg08_boss_rey_serpiente_dragon",
        "8|Basilisco Menor" to "dg08_basilisco_menor",
        "8|Cascabel de Muerte" to "dg08_cascabel_de_muerte",
        "8|Devorador de Veneno" to "dg08_devorador_de_veneno",
        "8|Gorgona de Hierro" to "dg08_gorgona_de_hierro",
        "8|Guerrero Cobra" to "dg08_guerrero_cobra",
        "8|Ilusionista Escamado" to "dg08_ilusionista_escamado",
        "8|Nagani Guardián" to "dg08_nagani_guardian",
        "8|Sacerdote Víbora" to "dg08_sacerdote_vibora",
        "9|Autómata de Bronce" to "dg09_automata_de_bronce",
        "9|BOSS" to "dg09_boss_igdrasil_el_cerebro_de_las_maquinas",
        "9|Centinela de Energía" to "dg09_centinela_de_energia",
        "9|Coloso Mecánico" to "dg09_coloso_mecanico",
        "9|Célula Voltáica" to "dg09_celula_voltaica",
        "9|Destructor de Titanio" to "dg09_destructor_de_titanio",
        "9|Dron Láser" to "dg09_dron_laser",
        "9|Ejecutor Cibernético" to "dg09_ejecutor_cibernetico",
        "9|Golem de Engranajes" to "dg09_golem_de_engranajes",
        "9|Núcleo de Plasma" to "dg09_nucleo_de_plasma",
    )

    fun dungeonKey(dungeonId: Int, rawName: String, isBoss: Boolean): String =
        DUNGEON[if (isBoss) "$dungeonId|BOSS" else "$dungeonId|$rawName"] ?: ""
}
