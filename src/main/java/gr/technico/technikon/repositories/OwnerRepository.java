package gr.technico.technikon.repositories;

import gr.technico.technikon.jpa.JpaUtil;
import gr.technico.technikon.model.Owner;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Owners.
 *
 */
@RequestScoped
public class OwnerRepository implements Repository<Owner, Long> {

    @PersistenceContext(unitName = "Technikon")
    private EntityManager entityManager;

    public OwnerRepository() {
    }

    /**
     * Constructs a new OwnerRepository with the the EntityManager
     *
     * @param entityManager
     */
    public OwnerRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Saves the Owner
     *
     * @param owner
     * @return an Optional containing the saved Owner, or an empty Optional if
     * the save failed
     */
    @Override
    @Transactional
    public Optional<Owner> save(Owner owner) {
        try {
            JpaUtil.beginTransaction();

            Owner managedOwner;
            if (owner.getId() == null || entityManager.find(Owner.class, owner.getId()) == null) {
                entityManager.persist(owner);
                managedOwner = owner;
            } else {
                managedOwner = entityManager.merge(owner);
            }
            JpaUtil.commitTransaction();
            return Optional.of(managedOwner);
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            return Optional.empty();
        }
    }

    /**
     * Finds an Owner by VAT
     *
     * @param vat
     * @return an Optional containing the found Owner or an empty Optional if no
     * Owner was found
     */
    public Optional<Owner> findByVat(String vat) {
        TypedQuery<Owner> query = entityManager.createQuery("FROM Owner WHERE vat = :vat", Owner.class);
        query.setParameter("vat", vat);
        return query.getResultStream().findFirst();
    }

    /**
     * Finds an Owner by email.
     *
     * @param email
     * @return an Optional containing the found Owner, or an empty Optional if
     * no Owner was found
     */
    public Optional<Owner> findByEmail(String email) {
        TypedQuery<Owner> query = entityManager.createQuery("FROM Owner WHERE email = :email", Owner.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }

    /**
     * Finds an Owner by username.
     *
     * @param username
     * @return an Optional containing the found Owner or an empty Optional if no
     * Owner was found
     */
    public Optional<Owner> findByUsername(String username) {
        TypedQuery<Owner> query = entityManager.createQuery("FROM Owner WHERE username = :username", Owner.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst();
    }

    /**
     * Finds an Owner by username and password.
     *
     * @param username
     * @param password
     * @return an Optional containing the found Owner, or an empty Optional if
     * no Owner was found
     */
    public Optional<Owner> findByUsernameAndPassword(String username, String password) {
        TypedQuery<Owner> query = entityManager.createQuery(
                "FROM Owner WHERE username = :username AND password = :password AND isDeleted = false",
                Owner.class
        );
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.getResultStream().findFirst();
    }

    public Optional<Owner> findByEmailAndPassword(String email, String password) {
        TypedQuery<Owner> query = entityManager.createQuery(
                "FROM Owner WHERE email = :email AND password = :password AND isDeleted = false",
                Owner.class
        );
        query.setParameter("email", email);
        query.setParameter("password", password);
        return query.getResultStream().findFirst();
    }

    /**
     * Permanently deletes an Owner by VAT.
     *
     * @param vat
     * @return true if the Owner was deleted, false otherwise
     */
    @Transactional
    public boolean deletePermanentlyByVat(String vat) {
        try {
            JpaUtil.beginTransaction();
            Optional<Owner> optionalOwner = findByVat(vat);

            if (optionalOwner.isPresent()) {
                Owner owner = optionalOwner.get();
                // Remove Owner and its Properties and Repairs.
                entityManager.remove(owner);
                JpaUtil.commitTransaction();
                return true;
            } else {
                JpaUtil.rollbackTransaction();
                return false;
            }
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            return false;
        }
    }

    @Override
    public Optional<Owner> findById(Long id) {
        Owner owner;
        try {
            owner = entityManager.find(getEntityClass(), id);
            return Optional.of(owner);
        } catch (Exception e) {

            throw e;
        }
    }

    @Override
    public List<Owner> findAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean deleteById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Class<Owner> getEntityClass() {
        return Owner.class;
    }

    private String getEntityClassName() {
        return Owner.class.getName();
    }
}
