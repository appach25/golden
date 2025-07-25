import psycopg2
from psycopg2 import Error

def connect_to_database():
    try:
        # Connection parameters - modify these as needed
        connection = psycopg2.connect(
            host="localhost",
            database="restaurant",
            user="postgres",
            password="postgres"  # Change this to your actual password
        )
        
        print("Successfully connected to PostgreSQL")
        return connection
    except (Exception, Error) as error:
        print(f"Error while connecting to PostgreSQL: {error}")
        return None

def create_tables(connection):
    try:
        cursor = connection.cursor()
        
        # Read and execute the SQL file
        with open('restaurant_tables.sql', 'r') as sql_file:
            cursor.execute(sql_file.read())
        
        connection.commit()
        print("Table created successfully")
        cursor.close()
        
    except (Exception, Error) as error:
        print(f"Error while creating table: {error}")

def main():
    connection = connect_to_database()
    if connection:
        create_tables(connection)
        connection.close()
        print("Database connection closed.")

if __name__ == "__main__":
    main()
