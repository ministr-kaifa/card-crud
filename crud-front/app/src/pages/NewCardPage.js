import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { useLocation, useNavigate } from 'react-router-dom';

const NewCardPage = () => {
  const location = useLocation();
  const ownerId = location.state.ownerId;
  const navigate = useNavigate();
  const apiHost = process.env.REACT_APP_CRUD_API_HOST;
  const [errorMessage, setErrorMessage] = useState('');
  const [newCard, setNewCard] = useState({
    ownerId: ownerId,
    cardNumber: '',
    validFrom: null,
    validTo: null
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewCard(prevCard => ({
      ...prevCard,
      [name]: value
    }));
  };

  const handleDateChange = (date, name) => {
    setNewCard(prevCard => ({
      ...prevCard,
      [name]: date.toISOString().substring(0, 10)
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(apiHost + '/api/cards', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newCard)
      });
      if (response.ok) {
        setErrorMessage(null);
        const fetchedNewCard = await response.json();
        navigate(`/cards/${fetchedNewCard.id}`);
        setNewCard({
          ownerId: ownerId,
          cardNumber: '',
          validFrom: null,
          validTo: null
        });
      } else {
        const fetchedErrorResponse = await response.json();
        if (fetchedErrorResponse.info === "non unique value") {
          setErrorMessage(fetchedErrorResponse.details);
        } else if (fetchedErrorResponse.info === "entity not found") {
          setErrorMessage(fetchedErrorResponse.details);
        } else if (fetchedErrorResponse.info === "validation errors") {
          setErrorMessage(fetchedErrorResponse.details.join(", "));
        }
      }
    } catch (error) {
      console.error('Ошибка при выполнении запроса:', error);
    }
  };

  return (
    <div>
      <label>
        Номер карты:
        <input
          type="text"
          name="cardNumber"
          value={newCard.cardNumber}
          onChange={handleChange}
        />
      </label>
      <label>
        Действительна с:
        <DatePicker
          selected={newCard.validFrom}
          onChange={(date) => handleDateChange(date, 'validFrom')}
          dateFormat="yyyy-MM-dd"
        />
      </label>
      <label>
        Действительна до:
        <DatePicker
          selected={newCard.validTo}
          onChange={(date) => handleDateChange(date, 'validTo')}
          dateFormat="yyyy-MM-dd"
        />
      </label>
      <button onClick={handleSubmit}>Сохранить</button>
      {errorMessage && <div style={{ color: 'red' }}>{errorMessage}</div>}
    </div>
  );
};

export { NewCardPage as NewCardForm };

