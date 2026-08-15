package com.example.data.engine

import com.example.data.GameProgress
import com.example.data.GameScreen
import kotlinx.coroutines.CoroutineScope

/**
 * Contrato mínimo que `GameViewModel` implementa para que el controlador nuevo
 * (`EldoriaSystemsController`) pueda leer progreso, persistirlo, navegar, avisar
 * y arrancar combates de expedición sin conocer nada de Compose ni de Android.
 */
interface EldoriaHost {

    /** Ámbito de corrutinas del ViewModel anfitrión. */
    val hostScope: CoroutineScope

    /** Progreso actual del jugador, o `null` si aún no hay partida cargada. */
    fun currentProgress(): GameProgress?

    /** Guarda el progreso actualizado (el anfitrión decide cuándo escribe en disco). */
    fun persistProgress(updated: GameProgress)

    /** Aviso no bloqueante para el jugador. */
    fun hostNotify(message: String)

    /** Cambia la pantalla activa. */
    fun hostNavigate(screen: GameScreen)

    /** Recalcula estadísticas derivadas (equipo, talentos, mascota) sobre el progreso dado. */
    fun hostSyncStats(progress: GameProgress): GameProgress

    /** Reproduce un efecto de sonido: "click","slash","crit","magic","heal","enemy","victory","defeat". */
    fun hostPlaySound(key: String)

    /** Arranca un combate dentro de una expedición con el contexto de la sala. */
    fun hostStartExpeditionCombat(
        dungeonId: Int,
        depth: Int,
        roomKind: String,
        roomLabel: String,
        hp: Int,
        mp: Int,
        bossName: String?
    )

    /** Entrega un objeto al inventario del jugador. */
    fun hostGrantItem(item: com.example.data.Item)
}
