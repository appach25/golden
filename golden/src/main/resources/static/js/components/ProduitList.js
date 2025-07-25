import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form } from 'react-bootstrap';
import axios from 'axios';

function ProduitList() {
    const [produits, setProduits] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [currentProduit, setCurrentProduit] = useState({
        nomProduit: '',
        description: '',
        type: 'DESSERT',
        imageProduit: ''
    });
    const [selectedFile, setSelectedFile] = useState(null);

    useEffect(() => {
        loadProduits();
    }, []);

    const loadProduits = async () => {
        try {
            const response = await axios.get('/api/produits');
            setProduits(response.data);
        } catch (error) {
            console.error('Error loading products:', error);
        }
    };

    const handleClose = () => {
        setShowModal(false);
        setCurrentProduit({
            nomProduit: '',
            description: '',
            type: 'DESSERT',
            imageProduit: ''
        });
        setSelectedFile(null);
    };

    const handleShow = (produit = null) => {
        if (produit) {
            setCurrentProduit(produit);
        }
        setShowModal(true);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCurrentProduit({
            ...currentProduit,
            [name]: value
        });
    };

    const handleFileChange = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            let imageUrl = currentProduit.imageProduit;

            if (selectedFile) {
                const formData = new FormData();
                formData.append('file', selectedFile);
                const uploadResponse = await axios.post('/api/upload/image', formData);
                imageUrl = uploadResponse.data;
            }

            const produitData = {
                ...currentProduit,
                imageProduit: imageUrl
            };

            if (currentProduit.id) {
                await axios.put(`/api/produits/${currentProduit.id}`, produitData);
            } else {
                await axios.post('/api/produits', produitData);
            }

            handleClose();
            loadProduits();
        } catch (error) {
            console.error('Error saving product:', error);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this product?')) {
            try {
                await axios.delete(`/api/produits/${id}`);
                loadProduits();
            } catch (error) {
                console.error('Error deleting product:', error);
            }
        }
    };

    return (
        <div className="container mt-4">
            <h2>Product Management</h2>
            <Button variant="primary" onClick={() => handleShow()} className="mb-3">
                Add New Product
            </Button>

            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Type</th>
                        <th>Image</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {produits.map(produit => (
                        <tr key={produit.id}>
                            <td>{produit.nomProduit}</td>
                            <td>{produit.description}</td>
                            <td>{produit.type}</td>
                            <td>
                                {produit.imageProduit && (
                                    <img 
                                        src={`/api/upload/image/${produit.imageProduit}`}
                                        alt={produit.nomProduit}
                                        style={{ maxHeight: '50px' }}
                                    />
                                )}
                            </td>
                            <td>
                                <Button variant="info" size="sm" onClick={() => handleShow(produit)} className="me-2">
                                    Edit
                                </Button>
                                <Button variant="danger" size="sm" onClick={() => handleDelete(produit.id)}>
                                    Delete
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>

            <Modal show={showModal} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        {currentProduit.id ? 'Edit Product' : 'Add New Product'}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3">
                            <Form.Label>Name</Form.Label>
                            <Form.Control
                                type="text"
                                name="nomProduit"
                                value={currentProduit.nomProduit}
                                onChange={handleInputChange}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Description</Form.Label>
                            <Form.Control
                                as="textarea"
                                name="description"
                                value={currentProduit.description}
                                onChange={handleInputChange}
                                rows={3}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Type</Form.Label>
                            <Form.Select
                                name="type"
                                value={currentProduit.type}
                                onChange={handleInputChange}
                            >
                                <option value="DESSERT">Dessert</option>
                                <option value="BOISSON">Boisson</option>
                                <option value="PLAT">Plat</option>
                                <option value="ICECREAM">Ice Cream</option>
                            </Form.Select>
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Image</Form.Label>
                            <Form.Control
                                type="file"
                                onChange={handleFileChange}
                                accept="image/*"
                            />
                            {currentProduit.imageProduit && (
                                <img
                                    src={`/api/upload/image/${currentProduit.imageProduit}`}
                                    alt="Preview"
                                    style={{ maxHeight: '100px', marginTop: '10px' }}
                                />
                            )}
                        </Form.Group>

                        <div className="text-end">
                            <Button variant="secondary" onClick={handleClose} className="me-2">
                                Cancel
                            </Button>
                            <Button variant="primary" type="submit">
                                Save
                            </Button>
                        </div>
                    </Form>
                </Modal.Body>
            </Modal>
        </div>
    );
}

export default ProduitList;
