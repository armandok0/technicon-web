package gr.technico.technikon.repositories;

import gr.technico.technikon.jpa.JpaUtil;
import gr.technico.technikon.model.Owner;
import gr.technico.technikon.model.Property;
import gr.technico.technikon.model.Repair;
import gr.technico.technikon.model.RepairStatus;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Repairs.
 *
 */
@RequestScoped
public class RepairRepository implements Repository<Repair, Long> {

    @PersistenceContext(unitName = "Technikon")
    private EntityManager entityManager;

    public RepairRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public RepairRepository() {
    }

    /**
     * Saves the provided Repair entity to the database.
     *
     * @param repair The repair entity to be saved. Must not be null.
     * @return An Optional containing the saved Repair if the save was
     * successful, or Optional.empty() if it was not.
     */
    @Override
    @Transactional
    public Optional<Repair> save(Repair repair) {
        if (repair.getId() == null) {
            entityManager.persist(repair);
        } else {
            entityManager.merge(repair);
        }
        return Optional.of(repair);
    }

    /**
     * Retrieves all Repair entities from the database.
     *
     * @return A List of all Repair entities.
     */
    @Override
    public List<Repair> findAll() {
        TypedQuery<Repair> query = entityManager.createQuery("from " + getEntityClassName(), getEntityClass());
        return query.getResultList();
    }

    /**
     * Finds a Repair entity by its ID.
     *
     * @param id The ID of the repair to be found. Must not be null.
     * @return An Optional containing the found Repair, or Optional.empty() if
     * no repair was found.
     */
    @Override
    public Optional<Repair> findById(Long id) {
        try {
            Repair repair = entityManager.find(getEntityClass(), id);
            if (repair != null) {
                return Optional.of(repair);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Deletes a Repair entity by its ID.
     *
     * @param id The ID of the repair to be deleted. Must not be null.
     * @return true if the repair was successfully deleted, false otherwise.
     */
    @Override
    @Transactional
    public boolean deleteById(Long id) {
        Repair repair = entityManager.find(getEntityClass(), id);
        if (repair != null) {

            try {
                JpaUtil.beginTransaction();
                entityManager.remove(repair);
                JpaUtil.commitTransaction();
            } catch (Exception e) {
                JpaUtil.rollbackTransaction();
                System.out.println("Exception: " + e);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Marks a Repair entity as deleted by setting its deleted flag to true and
     * saving the updated entity to the database.
     *
     * @param repair The repair entity to be safely deleted. Must not be null.
     * @return true if the repair was successfully marked as deleted, false
     * otherwise.
     */
    @Transactional
    public boolean safeDelete(Repair repair) {
        repair.setDeleted(true);
        Optional<Repair> safelyDeletedRepair = save(repair);
        if (safelyDeletedRepair.isPresent()) {
            return true;
        }
        return false;
    }

    /**
     * Finds all Repair entities associated with the given owner.
     *
     * @param owner The owner whose repairs are to be retrieved. Must not be
     * null.
     * @return A List of Repair entities associated with the specified owner.
     */
    public List<Repair> findRepairsByOwner(Owner owner) {
        TypedQuery<Repair> query = entityManager.createQuery(
                "FROM " + getEntityClassName()
                + " WHERE property.owner = :owner",
                getEntityClass())
                .setParameter("owner", owner);

        return query.getResultList();
    }

    /**
     * Finds all Repair entities with a status of "PENDING".
     *
     * @return A List of pending Repair entities.
     */
    public List<Repair> findPendingRepairs() {
        TypedQuery<Repair> query = entityManager.createQuery(
                "FROM " + getEntityClassName() + " WHERE repairStatus = :repairStatus",
                getEntityClass()
        ).setParameter("repairStatus", RepairStatus.PENDING);
        return query.getResultList();
    }

    /**
     *
     * Finds all Repair entities with a status of "PENDING" for a specific
     * owner.
     *
     * @param owner The owner whose pending repairs are to be retrieved. Must
     * not be null.
     * @return A List of pending Repair entities associated with the specified
     * owner.
     */
    public List<Repair> findPendingRepairsByOwner(Owner owner) {
        TypedQuery<Repair> query = entityManager.createQuery(
                "FROM " + getEntityClassName() + " WHERE repairStatus = :repairStatus AND property.owner = :owner",
                getEntityClass())
                .setParameter("repairStatus", RepairStatus.PENDING)
                .setParameter("owner", owner);

        return query.getResultList();
    }

    /**
     * Finds all Repair entities associated with a specific property.
     *
     * @param property The property whose repairs are to be retrieved. Must not
     * be null.
     * @return A List of Repair entities associated with the specified property.
     */
    public List<Repair> findRepairsByPropertyId(Property property) {
        TypedQuery<Repair> query = entityManager.createQuery("from " + getEntityClassName() + " where property = :property",
                getEntityClass())
                .setParameter("property", property);
        return query.getResultList();
    }

    /**
     * Finds all Repair entities with a status of "INPROGRESS".
     *
     * @return A List of in-progress Repair entities.
     */
    public List<Repair> findInProgressRepairs() {
        TypedQuery<Repair> query = entityManager.createQuery(
                "FROM " + getEntityClassName() + " WHERE repairStatus = :repairStatus",
                getEntityClass()
        ).setParameter("repairStatus", RepairStatus.INPROGRESS);
        return query.getResultList();
    }

    /**
     * Finds all Repair entities that have been accepted.
     *
     * @return A List of accepted Repair entities.
     */
    public List<Repair> findAcceptedRepairs() {
        TypedQuery<Repair> query = entityManager.createQuery(
                "from " + getEntityClassName() + " where acceptanceStatus = true",
                getEntityClass());
        return query.getResultList();
    }

    /**
     * Finds all Repair entities within a specific date range. Optionally
     * filters by owner.
     *
     * @param startDate The start date of the range. Must not be null.
     * @param endDate The end date of the range. Must not be null.
     * @param owner The owner to filter the repairs by. Can be null if no
     * filtering by owner is needed.
     */
    public List<Repair> findRepairsByDates(LocalDateTime startDate, LocalDateTime endDate, Owner owner) {
        Timestamp startTimestamp = Timestamp.valueOf(startDate);
        Timestamp endTimestamp = Timestamp.valueOf(endDate);

        if (owner == null) {
            TypedQuery<Repair> query = entityManager.createQuery(
                    "from " + getEntityClassName() + " where submissionDate between :startDate and :endDate",
                    getEntityClass())
                    .setParameter("startDate", startTimestamp)
                    .setParameter("endDate", endTimestamp);

            return query.getResultList();
        } else {
            TypedQuery<Repair> query = entityManager.createQuery(
                    "from " + getEntityClassName() + " where submissionDate between :startDate and :endDate" + " and property.owner = :owner", getEntityClass())
                    .setParameter("startDate", startTimestamp)
                    .setParameter("endDate", endTimestamp)
                    .setParameter("owner", owner);

            return query.getResultList();
        }
    }

    public Optional<Repair> findExistingRepair(Long propertyId, LocalDateTime submissionDate) {
        try {
            TypedQuery<Repair> query = entityManager.createQuery(
                    "SELECT r FROM Repair r WHERE r.property.id = :propertyId AND r.submissionDate = :submissionDate", Repair.class);
            query.setParameter("propertyId", propertyId);
            query.setParameter("submissionDate", Timestamp.valueOf(submissionDate));
            List<Repair> results = query.getResultList();
            if (!results.isEmpty()) {
                return Optional.of(results.get(0));
            }
        } catch (Exception e) {
        }
        return Optional.empty();
    }

    /**
     * Returns the Class object representing the Repair entity.
     *
     */
    private Class<Repair> getEntityClass() {
        return Repair.class;
    }

    /**
     * Returns the name of the Repair entity class.
     *
     * @return The name of the Repair entity class.
     */
    private String getEntityClassName() {
        return Repair.class.getName();
    }
}
