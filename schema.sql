

CREATE TABLE IF NOT EXISTS wallets (
  id            VARCHAR(64) PRIMARY KEY,
  type          VARCHAR(16) NOT NULL,
  address       VARCHAR(80) NOT NULL UNIQUE,
  balance       NUMERIC(36, 18) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS transactions (
  id            VARCHAR(64) PRIMARY KEY,
  wallet_id     VARCHAR(64) NOT NULL REFERENCES wallets(id),
  crypto_type   VARCHAR(16) NOT NULL,
  from_address  VARCHAR(100) NOT NULL,
  to_address    VARCHAR(100) NOT NULL,
  amount        NUMERIC(36, 18) NOT NULL,
  priority      VARCHAR(16) NOT NULL,
  fee           NUMERIC(36, 18) NOT NULL,
  status        VARCHAR(16) NOT NULL,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);







