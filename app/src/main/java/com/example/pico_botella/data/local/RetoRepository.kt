package com.example.pico_botella.data.local

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RetoRepository @Inject constructor(
    private val retoDao: RetoDao,
    private val firestore: FirebaseFirestore
) {

    // HU 8.0 y 13.0: Carga retos desde Firestore en lugar de SQLite
    val allRetos: Flow<List<RetoEntity>> = callbackFlow {
        val subscription = firestore.collection("retos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val retos = snapshot?.documents?.mapNotNull { doc ->
                    val descripcion = doc.getString("descripcion") ?: ""
                    val createdAt = doc.getLong("createdAt") ?: 0L
                    // Usamos el ID de Firestore como Int si es posible o manejamos la entidad
                    RetoEntity(id = doc.id.hashCode(), descripcion = descripcion, createdAt = createdAt)
                } ?: emptyList()
                trySend(retos)
            }
        awaitClose { subscription.remove() }
    }

    // HU 7.0: Guardar reto en Firestore
    suspend fun insert(reto: RetoEntity) {
        val data = hashMapOf(
            "descripcion" to reto.descripcion,
            "createdAt" to reto.createdAt
        )
        firestore.collection("retos").add(data).await()
    }

    // HU 8.0: Actualizar reto en Firestore
    suspend fun update(reto: RetoEntity) {
        // Nota: El ID en la entidad local es un Int por Room, 
        // pero en Firestore es un String. Para una migración completa 
        // se debería usar el ID de documento de Firestore.
        // Como guía, buscamos por descripción o necesitamos el docId real.
        val snapshot = firestore.collection("retos")
            .whereEqualTo("createdAt", reto.createdAt)
            .get()
            .await()
        
        for (doc in snapshot.documents) {
            doc.reference.update("descripcion", reto.descripcion).await()
        }
    }

    // HU 9.0: Eliminar reto de Firestore
    suspend fun delete(reto: RetoEntity) {
        val snapshot = firestore.collection("retos")
            .whereEqualTo("createdAt", reto.createdAt)
            .get()
            .await()
        
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }
}
