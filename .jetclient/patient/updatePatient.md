```toml
name = 'updatePatient'
method = 'PUT'
url = 'http://localhost:8081/patient/update/5'
sortWeight = 4000000
id = 'b4cc0914-f349-4f75-b464-0981a085fbc7'

[body]
type = 'JSON'
raw = '''
{
  "id": 5,
  "givenName": "Test3333",
  "familyName": "TestNone",
  "dateOfBirth": "1966",
  "sex": "F",
  "homeAddress": "1 Brrokside St",
  "phoneNumber": "100-222-3333"
}'''
```
