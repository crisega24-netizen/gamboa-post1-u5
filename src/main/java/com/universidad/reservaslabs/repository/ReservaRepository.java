package com.universidad.reservaslabs.repository;

import com.universidad.reservaslabs.model.EstadoReserva;
import com.universidad.reservaslabs.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByLaboratorioId(Long laboratorioId);

    // Consulta JPQL: reservas activas (no canceladas) del mismo laboratorio
    // cuyo rango [inicio, fin) se solapa con el rango dado. Se filtra en el
    // motor de base de datos, no en memoria, para que la consulta escale con
    // el volumen de reservas — ver Punto de decisión 1 sobre por qué el
    // filtrado vive aquí y no en el Service.
    @Query("""
         SELECT r FROM Reserva r
         WHERE r.laboratorio.id = :laboratorioId
           AND r.estado <> com.universidad.reservaslabs.model.EstadoReserva.CANCELADA
           AND r.inicio < :fin
           AND r.fin > :inicio
         """)
    List<Reserva> buscarSolapamientos(@Param("laboratorioId") Long laboratorioId,
                                           @Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin);
}