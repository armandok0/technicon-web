package gr.technico.technikon.repositories;

import gr.technico.technikon.jpa.JpaUtil;
import gr.technico.technikon.model.Property;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class PropertyRepository implements Repository<Property, Long> {

    @PersistenceContext(unitName = "Technikon")
    private EntityManager entityManager;

    public PropertyRepository() {
    }

    public PropertyRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Class<Property> getEntityClass() {
        return Property.class;
    }

    private String getEntityClassName() {
        return Property.class.getName();
    }

    /**
     * Saves the given Property entity to the database.
     *
     * This method starts a transaction, persists the Property entity, and
     * commits the transaction. If an exception occurs during the process, the
     * transaction is rolled back and an exception is thrown.
     *
     * @param property the Property entity to be saved
     * @return an Optional containing the saved Property entity if the operation
     * is successful
     * @throws Exception if an exception occurs during the save operation
     */
    @Override
    @Transactional
    public Optional<Property> save(Property property) {
        try {
            JpaUtil.beginTransaction();
            entityManager.merge(property);
            JpaUtil.commitTransaction();
            return Optional.of(property);
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            System.out.println("Exception: " + e);
            throw e;
        }
    }

    /**
     * Finds and returns an optional property based on the given property ID.
     *
     * @param id the ID of the property to search for.
     * @return an Optional containing the found property, or an empty Optional
     * if no property is found or an exception occurs.
     */
    @Override
    public Optional<Property> findById(Long id) {
        Property property;
        try {
            property = entityManager.find(getEntityClass(), id);
            return Optional.of(property);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return Optional.empty();
        }
    }

    /**
     * Retrieves all Property entities from the database.
     *
     *
     * @return a List containing all Property entities found in the database
     */
    @Override
    public List<Property> findAll() {
        TypedQuery<Property> query
                = entityManager.createQuery("from " + getEntityClassName(), getEntityClass());
        return query.getResultList();
    }

    /**
     * Deletes a Property entity by its ID.
     *
     * This method finds the Property entity with the specified ID. If the
     * entity is found, it starts a transaction, removes the entity from the
     * database, and commits the transaction. If an exception occurs during this
     * process, the transaction is rolled back and method returns false.
     *
     * @param id the ID of the Property entity to be deleted
     * @return true if the Property entity was found and successfully deleted;
     * false otherwise
     */
    @Override
    @Transactional
    public boolean deleteById(Long id) {
        Property property = entityManager.find(getEntityClass(), id);
        if (property != null) {

            try {
                JpaUtil.beginTransaction();
                entityManager.remove(property);
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
     * Finds and returns an optional property based on the given E9.
     *
     * @param e9 the E9 of the property to search for.
     * @return an Optional containing the found property, or an empty Optional
     * if no property is found.
     */
    public Optional<Property> findPropertyByE9(String e9) {
        TypedQuery<Property> query
                = entityManager.createQuery("from " + getEntityClassName()
                        + " where e9 = :e9 ",
                        getEntityClass())
                        .setParameter("e9", e9);
        return query.getResultStream().findFirst();
    }

    /**
     * Finds a list of Property entities by the owner's VAT identifier.
     *
     * This method creates a TypedQuery to search for Property} entities
     * associated with the specified owner's VAT identifier. The query returns a
     * list of properties that belong to the owner with the given VAT.
     *
     * @param vat the VAT identifier of the owner whose properties are to be
     * found
     * @return a List of Property entities associated with the specified owner's
     * VAT
     */
    public List<Property> findPropertyByVAT(String vat) {
        TypedQuery<Property> query = entityManager.createQuery(
                "FROM Property p WHERE p.owner.vat = :vat", Property.class)
                .setParameter("vat", vat);
        return query.getResultList();
    }

}
