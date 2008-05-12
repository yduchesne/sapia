require "person"

puts '

<html><body>
  <h1>JRuby Script Executed Successfully</h1>
'
p = Person.new("Foo Bar")

p.printName

puts '</body></html>'