import React from 'react';

const Login = () => {
  const login = () => {
    // Цей URL має вести на твій Gateway (порт 8081)
    window.location.href = 'http://localhost:8081/oauth2/authorization/google';
  };

  return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        flexDirection: 'column',
        gap: '20px'
      }}>
        <h1>Вхід у кінотеатр</h1>
        <button
            onClick={login}
            style={{
              padding: '15px 30px',
              fontSize: '18px',
              cursor: 'pointer',
              backgroundColor: '#4285F4',
              color: 'white',
              border: 'none',
              borderRadius: '5px',
              fontWeight: 'bold'
            }}
        >
          УВІЙТИ ЧЕРЕЗ GOOGLE
        </button>
      </div>
  );
};

export default Login;